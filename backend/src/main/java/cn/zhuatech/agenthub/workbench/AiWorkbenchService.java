/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.agenthub.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"steps");Map<String,List<String>> deps=new TreeMap<>();List<String> blocked=new ArrayList<>();
        for(var row:rows(r,"steps",1,200)){keys(row,"id","dependsOn","sideEffect","approved");String id=id(row,"id");if(deps.putIfAbsent(id,strings(row,"dependsOn",0,200))!=null)throw new IllegalArgumentException("任务标识重复");if(flag(row,"sideEffect")&&!flag(row,"approved"))blocked.add(id);}
        for(var entry:deps.entrySet()){Set<String> unique=new HashSet<>(entry.getValue());if(unique.size()!=entry.getValue().size()||!deps.keySet().containsAll(unique))throw new IllegalArgumentException("依赖重复或引用未知任务");}
        Set<String> done=new HashSet<>();List<String> order=new ArrayList<>();List<List<String>> levels=new ArrayList<>();
        while(done.size()<deps.size()){List<String> wave=deps.keySet().stream().filter(x->!done.contains(x)&&done.containsAll(deps.get(x))).toList();if(wave.isEmpty())break;levels.add(wave);order.addAll(wave);done.addAll(wave);}
        List<String> cycles=deps.keySet().stream().filter(x->!done.contains(x)).toList();Collections.sort(blocked);
        return Map.of("order",List.copyOf(order),"parallelLevels",List.copyOf(levels),"blockedSteps",List.copyOf(blocked),"unresolvedCycleOrDownstream",cycles,"readyToExecute",cycles.isEmpty()&&blocked.isEmpty(),"executesTools",false);

    }
}
