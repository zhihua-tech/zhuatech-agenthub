/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AgentToolPlanServiceTest {
    private final AgentToolPlanService service = new AgentToolPlanService(new ToolExecutionPolicyService());

    private ToolExecutionPolicyService.PolicyRequest step(String run, String tenant, String key,
                                                          double cost, boolean approval) {
        return new ToolExecutionPolicyService.PolicyRequest(run, tenant, tenant, "crm.customer.update",
                Set.of("crm.customer.update"), ToolExecutionPolicyService.Operation.WRITE,
                true, true, false, true, false, approval, 100, cost, key, false, true);
    }

    @Test
    void allowsApprovedPlanInsideAggregateBudget() {
        var result = service.evaluate(new AgentToolPlanService.PlanRequest(List.of(
                step("RUN-1", "tenant-a", "K-1", 3, true),
                step("RUN-1", "tenant-a", "K-2", 4, true)), 7));
        assertThat(result.decision()).isEqualTo(AgentToolPlanService.PlanDecision.READY);
        assertThat(result.estimatedTotalCost()).isEqualTo(7);
        assertThat(result.planFingerprint()).hasSize(64);
    }

    @Test
    void blocksAggregateOverspendEvenWhenEachStepFits() {
        var result = service.evaluate(new AgentToolPlanService.PlanRequest(List.of(
                step("RUN-1", "tenant-a", "K-1", 6, true),
                step("RUN-1", "tenant-a", "K-2", 6, true)), 10));
        assertThat(result.decision()).isEqualTo(AgentToolPlanService.PlanDecision.DENY);
        assertThat(result.blockers()).contains("计划累计预计费用超过剩余预算");
    }

    @Test
    void blocksCrossTenantAndDuplicateWriteKey() {
        var result = service.evaluate(new AgentToolPlanService.PlanRequest(List.of(
                step("RUN-1", "tenant-a", "K-1", 1, true),
                step("RUN-1", "tenant-b", "K-1", 1, true)), 10));
        assertThat(result.decision()).isEqualTo(AgentToolPlanService.PlanDecision.DENY);
        assertThat(result.blockers()).hasSize(2);
    }

    @Test
    void requiresApprovalWhenAnyStepNeedsIt() {
        var result = service.evaluate(new AgentToolPlanService.PlanRequest(List.of(
                step("RUN-1", "tenant-a", "K-1", 1, true),
                step("RUN-1", "tenant-a", "K-2", 1, false)), 10));
        assertThat(result.decision()).isEqualTo(AgentToolPlanService.PlanDecision.REVIEW);
    }
}
