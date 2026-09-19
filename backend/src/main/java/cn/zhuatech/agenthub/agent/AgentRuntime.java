/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.agent;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 智能体运行时边界；社区版使用本地演示执行器，不连接外部模型。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface AgentRuntime {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    AgentResult run(AgentRequest request);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    record AgentRequest(String objective, Map<String,String> context) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    record AgentStep(String name, String status, String evidence) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    record AgentResult(String runtime, String summary, List<AgentStep> steps, Map<String,Object> metrics) {}
}

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Component
class DemoAgentRuntime implements AgentRuntime {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentResult run(AgentRequest request) {
        return new AgentResult("local-governed-demo", "已生成受控执行计划，敏感工具调用等待人工审批。",
            List.of(new AgentStep("目标解析", "COMPLETED", "识别 3 项交付目标"), new AgentStep("工具规划", "COMPLETED", "匹配知识检索与工单工具"), new AgentStep("风险审批", "PENDING", "外发动作需要负责人确认")),
            Map.of("toolCandidates", 5, "policyChecks", 8, "objectiveLength", request.objective().length()));
    }
}
