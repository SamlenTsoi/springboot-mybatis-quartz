package samlen.tsoi.sbquartz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import samlen.tsoi.sbquartz.entity.DemoUser;
import samlen.tsoi.sbquartz.service.DemoUserWriteService;
import samlen.tsoi.sbquartz.service.dao.DemoUserMapper;

/**
 * @author samlen_tsoi
 * @date 2017/12/1
 */
@Service
public class DemoUserWriteServiceImpl implements DemoUserWriteService {

    @Autowired
    private DemoUserMapper sbquartzUserMapper;

    @Override
    public void insertOne(DemoUser sbquartzUser) {
        sbquartzUserMapper.insertSelective(sbquartzUser);
    }
}
