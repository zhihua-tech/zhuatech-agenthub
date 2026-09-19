/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.config;
import cn.zhuatech.agenthub.model.*; import cn.zhuatech.agenthub.repository.*; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.*; import org.springframework.security.crypto.password.PasswordEncoder; import java.time.LocalDate; import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration public class DataInitializer {/**
                                              * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                              */
@Bean CommandLineRunner seed(OperatingUnitRepository units,WorkRecordRepository records,ResourceRegisterRepository resources,ReviewRecordRepository reviews,UserRepository users,PasswordEncoder encoder){return args->{if(units.count()>0)return;
 OperatingUnit service=units.save(new OperatingUnit("AGT-SERVICE","客户服务智能体组","业务智能体中心",2400)),supply=units.save(new OperatingUnit("AGT-SUPPLY","供应链智能体组","运营智能体中心",1800)),knowledge=units.save(new OperatingUnit("AGT-KNOWLEDGE","知识运营智能体组","AI 治理中心",1200));
 WorkRecord a=records.save(new WorkRecord("RUN-260801-1042","AGENT-CUSTOMER-01","客户入驻资料核验与任务编排",service,120,87,2,LocalDate.now().plusDays(1),WorkRecord.Status.RUNNING,"FLOW-V3.2")); WorkRecord b=records.save(new WorkRecord("RUN-260801-0987","AGENT-SUPPLIER-02","供应商尽调与风险摘要",supply,96,96,1,LocalDate.now(),WorkRecord.Status.COMPLETED,"FLOW-V2.7")); WorkRecord c=records.save(new WorkRecord("RUN-260801-1061","AGENT-KNOWLEDGE-04","制度知识更新与冲突检测",knowledge,84,46,4,LocalDate.now().plusDays(2),WorkRecord.Status.RELEASED,"FLOW-V4.1"));
 resources.saveAll(List.of(new ResourceRegister("RUNTIME-PRD-01","生产智能体运行池",service,ResourceRegister.Status.RUNNING,94),new ResourceRegister("TOOLS-GATE-02","工具权限网关",supply,ResourceRegister.Status.RUNNING,98),new ResourceRegister("EVAL-SUITE-03","智能体评测集",knowledge,ResourceRegister.Status.ALARM,76)));
 reviews.saveAll(List.of(new ReviewRecord("EVAL-260801-032",a,"任务成功率",24,2,ReviewRecord.Result.PENDING,"沈策"),new ReviewRecord("EVAL-260801-019",b,"工具调用合规",18,0,ReviewRecord.Result.PASSED,"周屿"),new ReviewRecord("EVAL-260731-046",c,"幻觉与引用",20,3,ReviewRecord.Result.FAILED,"宋惟")));
 String demo=encoder.encode("Demo@2026"); users.saveAll(List.of(new UserAccount("operator",demo,"周屿",UserAccount.Role.DOMAIN_USER,"AGT-SERVICE"),new UserAccount("planner",demo,"沈策",UserAccount.Role.DOMAIN_OPERATOR,null),new UserAccount("quality",demo,"宋惟",UserAccount.Role.QUALITY,null),new UserAccount("admin",encoder.encode("ZhuaTech@2026"),"系统管理员",UserAccount.Role.ADMIN,null)));};}}
