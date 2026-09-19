/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.service;
import cn.zhuatech.agenthub.common.BusinessException; import cn.zhuatech.agenthub.dto.AgentHubDto.*; import cn.zhuatech.agenthub.model.*; import cn.zhuatech.agenthub.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service @Transactional(readOnly=true) public class AgentHubService {
    private final WorkRecordRepository orders; private final ActivityRecordRepository reports; private final ResourceRegisterRepository resources; private final ReviewRecordRepository reviewRecords; private final CurrentUserService current;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentHubService(WorkRecordRepository orders,ActivityRecordRepository reports,ResourceRegisterRepository resources,ReviewRecordRepository reviewRecords,CurrentUserService current){this.orders=orders;this.reports=reports;this.resources=resources;this.reviewRecords=reviewRecords;this.current=current;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Dashboard shopfloorDashboard(){String center=current.get().getOperatingUnitCode();List<WorkRecord> list=center==null?orders.findAllByOrderByDueDateAsc():orders.findByOperatingUnitCodeOrderByDueDateAsc(center);return dashboard(list);}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Dashboard adminDashboard(){return dashboard(orders.findAllByOrderByDueDateAsc());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public List<WorkRecordView> workRecords(){return orders.findAllByOrderByDueDateAsc().stream().map(this::view).toList();}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Transactional public ReportResult report(Long id,ReportRequest request){WorkRecord order=orders.findById(id).orElseThrow(()->new BusinessException("智能体任务不存在"));if(order.getStatus()==WorkRecord.Status.COMPLETED)throw new BusinessException("已完成任务不能继续提交");if(order.getCompletedQty()+request.goodQty()>order.getPlannedQty())throw new BusinessException("完成步骤不能超过计划步骤总数");order.report(request.goodQty(),request.defectQty());reports.save(new ActivityRecord(order,request.operationName(),request.goodQty(),request.defectQty(),current.get().getFullName(),request.remark()));return new ReportResult(order.getRecordNo(),order.getCompletedQty(),order.getDefectQty(),progress(order),order.getStatus().name());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private Dashboard dashboard(List<WorkRecord> list){int planned=list.stream().mapToInt(WorkRecord::getPlannedQty).sum(),done=list.stream().mapToInt(WorkRecord::getCompletedQty).sum(),defects=list.stream().mapToInt(WorkRecord::getDefectQty).sum();int rate=planned==0?0:Math.round(done*100f/planned);List<Metric> metrics=List.of(new Metric("计划步骤总量",String.format("%,d",planned),list.size()+" 个运行任务","blue"),new Metric("任务执行进度",rate+"%",String.format("%,d / %,d",done,planned),"green"),new Metric("有效完成率",String.format("%.1f%%",done+defects==0?100d:done*100d/(done+defects)),defects+" 个异常步骤","warn"),new Metric("治理风险",resources.countByStatus(ResourceRegister.Status.ALARM)+"",reviewRecords.countByResult(ReviewRecord.Result.PENDING)+" 项策略待审批","red"));return new Dashboard(metrics,list.stream().map(this::view).toList(),resources.findAllByOrderByCodeAsc().stream().map(e->new ControlView(e.getCode(),e.getName(),e.getOperatingUnit().getName(),e.getStatus().name(),e.getOee(),e.getLastHeartbeat())).toList(),reviewRecords.findTop10ByOrderByIdDesc().stream().map(i->new ReviewRecordView(i.getReviewRecordNo(),i.getWorkRecord().getRecordNo(),i.getWorkRecord().getSubjectName(),i.getReviewRecordType(),i.getReviewRecordQty(),i.getDefectQty(),i.getResult().name(),i.getInspector())).toList());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private WorkRecordView view(WorkRecord o){return new WorkRecordView(o.getId(),o.getRecordNo(),o.getSubjectCode(),o.getSubjectName(),o.getOperatingUnit().getName(),o.getOperatingUnit().getWorkshop(),o.getPlannedQty(),o.getCompletedQty(),o.getDefectQty(),o.getDueDate(),o.getStatus().name(),o.getVersionNo(),progress(o));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private int progress(WorkRecord o){return o.getPlannedQty()==0?0:Math.min(100,Math.round(o.getCompletedQty()*100f/o.getPlannedQty()));}
}
