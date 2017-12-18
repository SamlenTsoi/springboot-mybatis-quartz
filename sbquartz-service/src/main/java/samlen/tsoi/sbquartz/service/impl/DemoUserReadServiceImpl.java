package samlen.tsoi.sbquartz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import samlen.tsoi.sbquartz.entity.DemoUser;
import samlen.tsoi.sbquartz.service.DemoUserReadService;
import samlen.tsoi.sbquartz.service.dao.DemoUserMapper;

/**
 * @author samlen_tsoi
 * @date 2017/12/15
 */
@Service
public class DemoUserReadServiceImpl implements DemoUserReadService {

    @Autowired
    private DemoUserMapper demoUserMapper;

    @Override
    public DemoUser selectOneById(Long id) {
        return demoUserMapper.selectByPrimaryKey(id);
    }
}
