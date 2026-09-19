/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class CapacityRoutingService{/**
                                              * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                              */
public Result evaluate(Request r){int effective=Math.max(1,Math.min(r.availableWorkers(),r.modelConcurrentCapacity()));int wait=(int)Math.ceil(r.queuedRuns()*r.averageRunMinutes()/(double)effective);List<String> actions=new ArrayList<>();if(!r.toolsHealthy())actions.add("隔离异常工具并路由至降级执行池");if(wait>r.queueSlaMinutes())actions.add("扩容执行器或调整低优先级任务");if(r.highPriorityRuns()>0)actions.add("为高优先级任务预留并发槽位");String status=!r.toolsHealthy()?"DEGRADE":wait>r.queueSlaMinutes()?"ADD_CAPACITY":"NORMAL";if(actions.isEmpty())actions.add("执行容量满足队列 SLA");return new Result(effective,wait,status,actions);}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record Request(@Min(0) int queuedRuns,@Min(1) int availableWorkers,@Min(1) int modelConcurrentCapacity,@Min(1) int averageRunMinutes,@Min(1) int queueSlaMinutes,@Min(0) int highPriorityRuns,@NotNull Boolean toolsHealthy){}/**
                                                                                                                                                                                                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                       */
public record Result(int effectiveConcurrency,int estimatedWaitMinutes,String status,List<String> actions){} }
