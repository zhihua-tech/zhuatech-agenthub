/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolExecutionPolicyServiceTest {
    private final ToolExecutionPolicyService service = new ToolExecutionPolicyService();

    @Test
    void allowsReadOnlyToolWithScopedCredential() {
        var result = service.evaluate(request(ToolExecutionPolicyService.Operation.READ,
                true, false, true, false, null, false, true));
        assertThat(result.decision()).isEqualTo(ToolExecutionPolicyService.Decision.ALLOW_READ);
        assertThat(result.policyHash()).hasSize(64);
    }

    @Test
    void reviewsUnapprovedWrite() {
        var result = service.evaluate(request(ToolExecutionPolicyService.Operation.WRITE,
                false, false, true, false, "REQ-100", false, true));
        assertThat(result.decision()).isEqualTo(ToolExecutionPolicyService.Decision.REVIEW);
    }

    @Test
    void allowsApprovedExternalCommunication() {
        var result = service.evaluate(request(ToolExecutionPolicyService.Operation.EXTERNAL_COMMUNICATION,
                true, true, true, false, "REQ-101", false, true));
        assertThat(result.decision()).isEqualTo(ToolExecutionPolicyService.Decision.ALLOW_EXECUTE);
    }

    @Test
    void preventsDuplicateSideEffect() {
        var result = service.evaluate(request(ToolExecutionPolicyService.Operation.WRITE,
                true, false, true, false, "REQ-102", true, true));
        assertThat(result.decision()).isEqualTo(ToolExecutionPolicyService.Decision.REPLAY_SAFE);
        assertThat(result.obligations()).contains("返回首次执行结果，禁止重复调用外部系统");
    }

    @Test
    void deniesSecretAndUnapprovedPii() {
        var result = service.evaluate(request(ToolExecutionPolicyService.Operation.READ,
                true, true, false, true, null, false, true));
        assertThat(result.decision()).isEqualTo(ToolExecutionPolicyService.Decision.DENY);
        assertThat(result.blockers()).hasSize(2);
    }

    private ToolExecutionPolicyService.PolicyRequest request(
            ToolExecutionPolicyService.Operation operation, boolean approval, boolean pii,
            boolean piiApproved, boolean secret, String idempotencyKey, boolean duplicate,
            boolean destinationAllowed) {
        return new ToolExecutionPolicyService.PolicyRequest("RUN-100", "tenant-a", "tenant-a",
                "crm.customer.lookup", Set.of("crm.customer.lookup", "crm.case.update"), operation,
                true, true, pii, piiApproved, secret, approval, 100, 2, idempotencyKey, duplicate,
                destinationAllowed);
    }
}
