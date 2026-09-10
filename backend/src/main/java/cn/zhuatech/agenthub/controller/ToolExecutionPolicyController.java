/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.controller;

import cn.zhuatech.agenthub.common.ApiResponse;
import cn.zhuatech.agenthub.service.ToolExecutionPolicyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/agenthub")
public class ToolExecutionPolicyController {
    private final ToolExecutionPolicyService service;

    public ToolExecutionPolicyController(ToolExecutionPolicyService service) {
        this.service = service;
    }

    @PostMapping("/tool-execution-policy")
    public ApiResponse<ToolExecutionPolicyService.PolicyResult> evaluate(
            @Valid @RequestBody ToolExecutionPolicyService.PolicyRequest request) {
        return ApiResponse.ok("Agent 工具执行策略决策完成", service.evaluate(request));
    }
}
