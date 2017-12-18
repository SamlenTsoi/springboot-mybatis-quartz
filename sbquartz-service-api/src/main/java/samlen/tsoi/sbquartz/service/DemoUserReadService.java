package samlen.tsoi.sbquartz.service;

import samlen.tsoi.sbquartz.entity.DemoUser;

/**
 * @author samlen_tsoi
 * @date 2017/12/15
 */
public interface DemoUserReadService {

    /**
     * 根据主键查询用户
     * @param id
     * @return
     */
    DemoUser selectOneById(Long id);
}
