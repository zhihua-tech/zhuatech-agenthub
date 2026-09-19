/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.agent;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 在智能体进入执行队列前估算令牌、费用和工具调用预算。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AgentBudgetService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public BudgetResult evaluate(BudgetRequest request) {
        BigDecimal projectedCost = request.costPerThousandTokens()
            .multiply(BigDecimal.valueOf(request.estimatedTokens()))
            .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        int tokenUtilization = (int) Math.min(999,
            Math.round(request.estimatedTokens() * 100.0 / request.tokenLimit()));
        List<String> violations = new ArrayList<>();
        if (request.estimatedTokens() > request.tokenLimit()) violations.add("预计令牌超过单次上限");
        if (projectedCost.compareTo(request.costLimit()) > 0) violations.add("预计费用超过单次预算");
        if (request.plannedToolCalls() > request.toolCallLimit()) violations.add("计划工具调用次数超过上限");

        String decision = !violations.isEmpty() ? "BLOCKED" : tokenUtilization >= 85 ? "WATCH" : "READY";
        return new BudgetResult(
            decision,
            projectedCost,
            tokenUtilization,
            Math.max(0, request.tokenLimit() - request.estimatedTokens()),
            List.copyOf(violations),
            "BLOCKED".equals(decision) ? "调整模型、上下文或工具计划后重新评估"
                : "WATCH".equals(decision) ? "允许执行，但需开启预算告警" : "预算充足，可进入受控执行队列"
        );
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BudgetRequest(
        @NotBlank(message = "请输入智能体名称") String agentName,
        @Positive int estimatedTokens,
        @Positive int tokenLimit,
        @DecimalMin("0.0") BigDecimal costPerThousandTokens,
        @DecimalMin("0.0") BigDecimal costLimit,
        @Positive int plannedToolCalls,
        @Positive int toolCallLimit
    ) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record BudgetResult(
        String decision,
        BigDecimal projectedCost,
        int tokenUtilizationPercent,
        int remainingTokens,
        List<String> violations,
        String guidance
    ) {}
}
