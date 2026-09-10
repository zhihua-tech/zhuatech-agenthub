/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub;
import org.junit.jupiter.api.*; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc; import java.util.regex.*; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class AgentHubApiIntegrationTests {
    @Autowired MockMvc mvc; private String operatorToken; private String plannerToken;
    @BeforeEach void login()throws Exception{operatorToken=token("operator","Demo@2026");plannerToken=token("planner","Demo@2026");}
    private String token(String u,String p)throws Exception{String json=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\""+u+"\",\"password\":\""+p+"\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();Matcher matcher=Pattern.compile("\\\"token\\\":\\\"([^\\\"]+)\\\"").matcher(json);if(!matcher.find())throw new AssertionError("登录响应中缺少 token");return matcher.group(1);}
    @Test void operatorCanReadShopfloorDashboard()throws Exception{mvc.perform(get("/api/shopfloor/dashboard").header("Authorization","Bearer "+operatorToken)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.metrics[0].label").value("计划步骤总量"));}
    @Test void plannerCanReadWorkRecords()throws Exception{mvc.perform(get("/api/admin/work-orders").header("Authorization","Bearer "+plannerToken)).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(3));}
    @Test void operatorCanSubmitProductionReport()throws Exception{mvc.perform(post("/api/shopfloor/work-orders/1/reports").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"operationName\":\"人工审批\",\"goodQty\":2,\"defectQty\":1,\"remark\":\"证据完整\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("反馈提交成功")).andExpect(jsonPath("$.data.completedQty").value(89));}
    @Test void operatorCanRunLocalAgentPreview()throws Exception{mvc.perform(post("/api/shopfloor/agent-preview").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"objective\":\"核验客户资料\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.runtime").value("local-governed-demo")).andExpect(jsonPath("$.data.steps.length()").value(3));}
    @Test void operatorCanRunAgentGovernancePreflight()throws Exception{mvc.perform(post("/api/shopfloor/agent-preflight").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"objective\":\"生成客户报告并发送\",\"tools\":[\"knowledge.search\",\"message.send\"],\"externalWrite\":true,\"sensitiveData\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("治理检查完成"))
        .andExpect(jsonPath("$.data.decision").value("BLOCKED"))
        .andExpect(jsonPath("$.data.blockedTools[0]").value("message.send"))
        .andExpect(jsonPath("$.data.requiredApprovals.length()").value(3));}
    @Test void operatorCanEvaluateAgentBudget()throws Exception{mvc.perform(post("/api/shopfloor/agent-budget").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"agentName\":\"客户报告智能体\",\"estimatedTokens\":9000,\"tokenLimit\":8000,\"costPerThousandTokens\":0.2,\"costLimit\":1.5,\"plannedToolCalls\":4,\"toolCallLimit\":3}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("执行预算评估完成"))
        .andExpect(jsonPath("$.data.decision").value("BLOCKED"))
        .andExpect(jsonPath("$.data.projectedCost").value(1.8))
        .andExpect(jsonPath("$.data.violations.length()").value(3));}
    @Test void operatorCanEvaluateToolExecutionPolicy()throws Exception{mvc.perform(post("/api/enterprise/agenthub/tool-execution-policy").header("Authorization","Bearer "+operatorToken).contentType(MediaType.APPLICATION_JSON).content("{\"runId\":\"RUN-100\",\"tenantId\":\"tenant-a\",\"agentTenantId\":\"tenant-a\",\"toolName\":\"crm.customer.lookup\",\"allowedTools\":[\"crm.customer.lookup\"],\"operation\":\"READ\",\"identityVerified\":true,\"scopedCredential\":true,\"piiDetected\":false,\"piiApproved\":false,\"secretDetected\":false,\"humanApproval\":false,\"budgetRemaining\":100,\"estimatedCost\":2,\"duplicateRequest\":false,\"destinationAllowlisted\":true}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.data.decision").value("ALLOW_READ"))
        .andExpect(jsonPath("$.data.policyHash").isNotEmpty());}
    @Test void anonymousRequestIsDenied()throws Exception{mvc.perform(get("/api/admin/dashboard")).andExpect(status().isForbidden());}
}
