/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AgentProductionAuthorizationService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.ownerAssigned()) blockers.add("Agent 业务与技术责任人未指定");
        if (!request.toolPermissionsLeastPrivilege()) blockers.add("工具权限未按最小权限配置");
        if (!request.secretsInVault()) blockers.add("工具凭据未纳入凭据库");
        if (!request.promptInjectionTestsPassed()) blockers.add("提示词注入安全测试未通过");
        if (!request.humanApprovalForHighImpact()) blockers.add("高影响动作未配置人工审批");
        if (!request.auditTrailEnabled()) blockers.add("执行审计轨迹未启用");
        if (!request.killSwitchReady()) blockers.add("Agent 紧急停止机制未就绪");
        if (request.evalPassRate() < request.minEvalPassRate()) blockers.add("Agent 评测通过率低于上线阈值");
        if (request.openCriticalFindings() > 0) blockers.add("存在未关闭的严重风险项");
        if (!blockers.isEmpty()) {
            actions.add("阻断上线并补齐安全、评测和责任控制");
            return new Assessment(Decision.BLOCKED, blockers, actions);
        }
        if (!request.capacityPlanValidated() || !request.costBudgetApproved()) {
            if (!request.capacityPlanValidated()) actions.add("完成并发、队列和降级容量验证");
            if (!request.costBudgetApproved()) actions.add("完成模型与工具调用预算审批");
            return new Assessment(Decision.PILOT, blockers, actions);
        }
        actions.add("批准生产运行并持续监控安全、质量、成本和人工接管");
        return new Assessment(Decision.AUTHORIZE, blockers, actions);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String agentId, boolean ownerAssigned, boolean toolPermissionsLeastPrivilege,
                          boolean secretsInVault, boolean promptInjectionTestsPassed,
                          boolean humanApprovalForHighImpact, boolean auditTrailEnabled,
                          boolean killSwitchReady, @DecimalMin("0.0") double evalPassRate,
                          @DecimalMin("0.0") double minEvalPassRate, @Min(0) int openCriticalFindings,
                          boolean capacityPlanValidated, boolean costBudgetApproved) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Assessment(Decision decision, List<String> blockers, List<String> actions) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Decision { AUTHORIZE, PILOT, BLOCKED }
}
