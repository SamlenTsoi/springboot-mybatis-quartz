package samlen.tsoi.sbquartz.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samlen.tsoi.sbquartz.entity.DemoUser;
import samlen.tsoi.sbquartz.service.DemoUserWriteService;

/**
 * @author samlen_tsoi
 * @date 2017/12/1
 */
@Slf4j
@RestController
@RequestMapping("user")
public class DemoUserController {

    @Autowired
    private DemoUserWriteService sbquartzUserWriteService;

    @PostMapping("add")
    public void create(@RequestBody DemoUser sbquartzUser) {
        sbquartzUserWriteService.insertOne(sbquartzUser);
    }
}
