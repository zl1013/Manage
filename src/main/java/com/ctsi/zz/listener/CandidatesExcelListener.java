package com.ctsi.zz.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ctsi.zz.domain.CandidateData;
import com.ctsi.zz.domain.Candidates;
import com.ctsi.zz.service.CandidatesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:
 * Created by 乍暖还寒 on 2020/8/31 12:24
 * Version 1.0
 */
//需要手动new，不能通过注解交给spring管理，不能注入其他对象
//不能实现数据库操作
//可通过构造方法直接注入
public class CandidatesExcelListener extends AnalysisEventListener<CandidateData> {


    @Autowired
    private CandidatesService candidatesService;


    public CandidatesExcelListener() {
    }

    public CandidatesExcelListener(CandidatesService candidatesService) {
        this.candidatesService = candidatesService;
    }


    @Override
    public void invoke(CandidateData candidateData, AnalysisContext analysisContext) {
        System.out.println("开始读取Excel");
        if (candidateData == null) {
            try {
                throw new Exception("文件内容为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            //一行一行读取
            Candidates candidate = this.existCandidates(candidatesService, candidateData.getPhone());
            //为空则说明数据库中不存在该记录
            if (candidate == null) {
                candidate = new Candidates();
                candidate.setName(candidateData.getName());
                candidate.setPhone(candidateData.getPhone());
                BeanUtils.copyProperties(candidateData,candidate);
                candidatesService.insert(candidate);
                System.out.println("添加成功");
            }
            System.out.println(candidate.getPhone()+"已存在");
        }
    }

    //判断手机号码是否存在
    private Candidates existCandidates(CandidatesService candidatesService, String phone) {
        Candidates candidates = candidatesService.findOne(phone);
        return candidates;
    }

    //读取完成之后执行的操作
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
