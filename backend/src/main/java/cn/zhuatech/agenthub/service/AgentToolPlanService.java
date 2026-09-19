/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对完整工具计划做跨步骤的租户、幂等键和累计预算门禁。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AgentToolPlanService {
    private final ToolExecutionPolicyService toolPolicy;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentToolPlanService(ToolExecutionPolicyService toolPolicy) {
        this.toolPolicy = toolPolicy;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public PlanResult evaluate(PlanRequest request) {
        if (request.steps() == null || request.steps().isEmpty()) {
            throw new IllegalArgumentException("工具计划不能为空");
        }
        var first = request.steps().getFirst();
        List<String> blockers = new ArrayList<>();
        List<ToolExecutionPolicyService.PolicyResult> stepResults = new ArrayList<>();
        Set<String> writeKeys = new HashSet<>();
        double totalCost = 0;
        for (int i = 0; i < request.steps().size(); i++) {
            var step = request.steps().get(i);
            if (!first.runId().equals(step.runId()) || !first.tenantId().equals(step.tenantId())) {
                blockers.add("第 " + (i + 1) + " 步与计划运行或租户不一致");
            }
            if (step.operation() != ToolExecutionPolicyService.Operation.READ
                    && step.idempotencyKey() != null && !step.idempotencyKey().isBlank()
                    && !writeKeys.add(step.idempotencyKey())) {
                blockers.add("第 " + (i + 1) + " 步重复使用写操作幂等键");
            }
            totalCost += step.estimatedCost();
            stepResults.add(toolPolicy.evaluate(step));
        }
        if (totalCost > request.totalBudgetRemaining()) blockers.add("计划累计预计费用超过剩余预算");
        if (stepResults.stream().anyMatch(result -> result.decision() == ToolExecutionPolicyService.Decision.DENY)) {
            blockers.add("至少一个工具未通过单步执行策略");
        }
        PlanDecision decision = !blockers.isEmpty() ? PlanDecision.DENY
                : stepResults.stream().anyMatch(result -> result.decision() == ToolExecutionPolicyService.Decision.REVIEW)
                ? PlanDecision.REVIEW : PlanDecision.READY;
        return new PlanResult(decision, Math.round(totalCost * 10000d) / 10000d,
                List.copyOf(stepResults), List.copyOf(blockers), fingerprint(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String fingerprint(PlanRequest request) {
        StringBuilder source = new StringBuilder().append(request.totalBudgetRemaining());
        for (var step : request.steps()) {
            source.append('|').append(step.runId()).append('|').append(step.tenantId())
                    .append('|').append(step.toolName()).append('|').append(step.operation())
                    .append('|').append(step.estimatedCost()).append('|').append(step.idempotencyKey());
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(source.toString().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PlanRequest(@NotEmpty @Valid List<ToolExecutionPolicyService.PolicyRequest> steps,
                              @DecimalMin("0.0") double totalBudgetRemaining) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PlanResult(PlanDecision decision, double estimatedTotalCost,
                             List<ToolExecutionPolicyService.PolicyResult> steps,
                             List<String> blockers, String planFingerprint) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum PlanDecision { READY, REVIEW, DENY }
}
