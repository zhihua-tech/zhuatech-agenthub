/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** 在 Agent 工具真正执行前完成租户、权限、参数、预算、审批和幂等策略决策。 */
@Service
public class ToolExecutionPolicyService {
    public PolicyResult evaluate(PolicyRequest request) {
        Set<String> allowedTools = request.allowedTools() == null ? Set.of() : Set.copyOf(request.allowedTools());
        List<String> blockers = new ArrayList<>();
        List<String> obligations = new ArrayList<>();

        if (!request.identityVerified()) blockers.add("调用身份未验证");
        if (!request.tenantId().equals(request.agentTenantId())) blockers.add("Agent 与请求租户不一致");
        if (!allowedTools.contains(request.toolName())) blockers.add("工具不在 Agent 允许清单");
        if (!request.scopedCredential()) blockers.add("未使用最小权限凭据");
        if (request.secretDetected()) blockers.add("工具参数包含密钥或令牌");
        if (request.estimatedCost() > request.budgetRemaining()) blockers.add("预计费用超过任务剩余预算");
        if (request.piiDetected() && !request.piiApproved()) blockers.add("个人信息处理未获授权");
        if (request.operation() == Operation.EXTERNAL_COMMUNICATION && !request.destinationAllowlisted()) {
            blockers.add("外部通信目标不在允许清单");
        }

        boolean stateChanging = request.operation() != Operation.READ;
        if (stateChanging && blank(request.idempotencyKey())) blockers.add("写操作缺少幂等键");

        if (!blockers.isEmpty()) {
            obligations.add("拒绝工具执行并写入策略审计日志");
            return result(Decision.DENY, request, blockers, obligations);
        }

        if (request.duplicateRequest()) {
            obligations.add("返回首次执行结果，禁止重复调用外部系统");
            return result(Decision.REPLAY_SAFE, request, blockers, obligations);
        }

        if (stateChanging && !request.humanApproval()) {
            obligations.add("由业务责任人审批工具名称、目标和参数摘要");
            if (request.operation() == Operation.DELETE) obligations.add("删除操作必须确认恢复点和影响范围");
            if (request.operation() == Operation.EXTERNAL_COMMUNICATION) {
                obligations.add("外发内容必须经人工确认并保留送达证据");
            }
            return result(Decision.REVIEW, request, blockers, obligations);
        }

        obligations.add("记录工具版本、参数摘要、耗时、费用和执行结果");
        if (stateChanging) obligations.add("以幂等键关联执行结果并启用失败补偿");
        Decision decision = stateChanging ? Decision.ALLOW_EXECUTE : Decision.ALLOW_READ;
        return result(decision, request, blockers, obligations);
    }

    private PolicyResult result(Decision decision, PolicyRequest request,
                                List<String> blockers, List<String> obligations) {
        return new PolicyResult(decision, List.copyOf(blockers), List.copyOf(obligations), policyHash(request));
    }

    private String policyHash(PolicyRequest request) {
        Set<String> allowedTools = request.allowedTools() == null ? Set.of() : request.allowedTools();
        String source = String.join("|", request.tenantId(), request.agentTenantId(), request.toolName(),
                request.operation().name(), String.join(",", new TreeSet<>(allowedTools)),
                String.valueOf(request.piiApproved()), String.valueOf(request.destinationAllowlisted()));
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    public record PolicyRequest(
            @NotBlank String runId,
            @NotBlank String tenantId,
            @NotBlank String agentTenantId,
            @NotBlank String toolName,
            @NotEmpty Set<String> allowedTools,
            @NotNull Operation operation,
            boolean identityVerified,
            boolean scopedCredential,
            boolean piiDetected,
            boolean piiApproved,
            boolean secretDetected,
            boolean humanApproval,
            @DecimalMin("0.0") double budgetRemaining,
            @DecimalMin("0.0") double estimatedCost,
            String idempotencyKey,
            boolean duplicateRequest,
            boolean destinationAllowlisted
    ) {}

    public record PolicyResult(Decision decision, List<String> blockers,
                               List<String> obligations, String policyHash) {}

    public enum Operation { READ, WRITE, DELETE, EXTERNAL_COMMUNICATION }
    public enum Decision { ALLOW_READ, ALLOW_EXECUTE, REVIEW, REPLAY_SAFE, DENY }
}
