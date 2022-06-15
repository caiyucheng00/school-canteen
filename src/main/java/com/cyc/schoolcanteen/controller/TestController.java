package com.cyc.schoolcanteen.controller;

import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-06-14 14:39
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/get")
    public Result<User> get(@RequestParam String name){
        log.info(name);
        User user = new User();
        user.setName("我是java");
        user.setPhone("123");
        return Result.success(user);
    }

    @PostMapping("/post")
    public Result<String> post(@RequestBody Map<String,Object> map){
        log.info((String) map.get("name"));
        Integer id = (Integer) map.get("id");
        log.info(String.valueOf(id));

        return Result.success("成功POST");
    }
}
