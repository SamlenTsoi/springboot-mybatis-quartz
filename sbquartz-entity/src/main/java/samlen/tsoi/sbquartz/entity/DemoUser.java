package samlen.tsoi.sbquartz.entity;

import java.util.Date;
import lombok.Data;

@Data
public class DemoUser {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别，1：未知，2：男，3：女
     */
    private Integer gender;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}