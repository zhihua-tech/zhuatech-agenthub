/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 在智能体真正执行工具前完成最小权限和人工审批检查。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AgentGovernanceService {
    private static final Set<String> REGISTERED_TOOLS = Set.of(
        "knowledge.search", "ticket.read", "ticket.comment", "customer.read", "report.generate"
    );

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public PreflightResult preflight(PreflightRequest request) {
        List<String> normalizedTools = request.tools().stream()
            .map(String::trim)
            .filter(tool -> !tool.isEmpty())
            .distinct()
            .toList();
        List<String> blockedTools = normalizedTools.stream()
            .filter(tool -> !REGISTERED_TOOLS.contains(tool))
            .toList();

        int riskScore = Math.min(100,
            (request.externalWrite() ? 45 : 0)
                + (request.sensitiveData() ? 30 : 0)
                + blockedTools.size() * 20
                + (normalizedTools.size() > 3 ? 10 : 0));

        LinkedHashSet<String> approvals = new LinkedHashSet<>();
        if (request.externalWrite()) approvals.add("业务负责人审批");
        if (request.sensitiveData()) approvals.add("数据安全审批");
        if (!blockedTools.isEmpty()) approvals.add("平台管理员登记工具");

        String decision = !blockedTools.isEmpty() ? "BLOCKED" : riskScore >= 40 ? "REVIEW" : "APPROVED";
        return new PreflightResult(
            decision,
            riskScore,
            List.copyOf(approvals),
            blockedTools,
            normalizedTools.size(),
            "BLOCKED".equals(decision) ? "移除或登记未授权工具后重新检查" : "REVIEW".equals(decision) ? "审批完成后方可执行" : "可进入受控执行队列"
        );
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PreflightRequest(
        @NotBlank(message = "请输入智能体目标") String objective,
        @NotEmpty(message = "请至少选择一个工具") List<String> tools,
        boolean externalWrite,
        boolean sensitiveData
    ) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record PreflightResult(
        String decision,
        int riskScore,
        List<String> requiredApprovals,
        List<String> blockedTools,
        int toolCount,
        String nextAction
    ) {}
}
