package samlen.tsoi.sbquartz.service.dao;

import samlen.tsoi.sbquartz.entity.DemoUser;

public interface DemoUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DemoUser record);

    int insertSelective(DemoUser record);

    DemoUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DemoUser record);

    int updateByPrimaryKey(DemoUser record);
}