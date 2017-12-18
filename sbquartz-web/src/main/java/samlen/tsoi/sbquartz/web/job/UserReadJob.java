package samlen.tsoi.sbquartz.web.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import samlen.tsoi.sbquartz.entity.DemoUser;
import samlen.tsoi.sbquartz.service.DemoUserReadService;

import java.time.Instant;
import java.util.Date;

/**
 * @author samlen_tsoi
 * @date 2017/12/15
 */
@Slf4j
@Component
public class UserReadJob implements Job {

    @Autowired
    private DemoUserReadService demoUserReadService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DemoUser demoUser = demoUserReadService.selectOneById(1L);
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        log.info("当前时间为{}, extra : {}, 当前jobDetail : {}", Date.from(Instant.now()), jobDetail.getJobDataMap().get("extra"), jobDetail);
        log.info("用户信息 : {}", demoUser);
    }
}
