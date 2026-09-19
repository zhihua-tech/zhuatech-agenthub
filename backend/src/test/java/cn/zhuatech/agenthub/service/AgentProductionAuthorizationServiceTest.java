/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class AgentProductionAuthorizationServiceTest {
    private final AgentProductionAuthorizationService service = new AgentProductionAuthorizationService();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void authorizesControlledAgent() {
        var result = service.assess(new AgentProductionAuthorizationService.Request("A1", true, true, true,
                true, true, true, true, 95, 90, 0, true, true));
        assertThat(result.decision()).isEqualTo(AgentProductionAuthorizationService.Decision.AUTHORIZE);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void pilotsCapacityAndBudgetGaps() {
        var result = service.assess(new AgentProductionAuthorizationService.Request("A2", true, true, true,
                true, true, true, true, 95, 90, 0, false, false));
        assertThat(result.actions()).hasSize(2);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void blocksUnsafeAgent() {
        var result = service.assess(new AgentProductionAuthorizationService.Request("A3", false, false, false,
                false, false, false, false, 60, 90, 2, true, true));
        assertThat(result.blockers()).hasSize(9);
    }
}
