/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.controller;import cn.zhuatech.agenthub.common.ApiResponse;import cn.zhuatech.agenthub.service.CapacityRoutingService;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/agenthub/insights/capacity-routing") public class CapacityRoutingController{private final CapacityRoutingService service;/**
                                                                                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                */
public CapacityRoutingController(CapacityRoutingService service){this.service=service;}/**
                                                                                                                                                                                                                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                       */
@PostMapping ApiResponse<CapacityRoutingService.Result> evaluate(@Valid @RequestBody CapacityRoutingService.Request r){return ApiResponse.ok(service.evaluate(r));}}
