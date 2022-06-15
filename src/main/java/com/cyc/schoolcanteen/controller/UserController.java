package com.cyc.schoolcanteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cyc.schoolcanteen.dto.Result;
import com.cyc.schoolcanteen.entity.User;
import com.cyc.schoolcanteen.service.UserService;
import com.cyc.schoolcanteen.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 虚幻的元亨利贞
 * @Description
 * @date 2022-05-30 11:18
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取验证码
     *
     * @param user
     * @param session
     */
    @PostMapping("/code")
    public void sendCode(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        Integer code = ValidateCodeUtils.generateValidateCode(4);

        log.info(code.toString());
        // 保存验证码
        session.setAttribute(phone, code);
    }


    /**
     * 采用验证码登陆
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> map, HttpSession session) {
        //1. 比对验证码信息
        String phone = map.get("phone");
        String code = map.get("code");
        Integer attribute = (Integer) session.getAttribute(phone);

        if (attribute != null) {
            int codeInt = Integer.parseInt(code);
            if (codeInt == attribute) {
                log.info("用户登陆成功");

                //2. 若是新用户则加入表
                User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
                if(user == null){
                    log.info("新用户登陆成功");
                    User newUser = new User();
                    newUser.setPhone(phone);
                    newUser.setName("手机用户_" + phone);
                    userService.save(newUser);
                    user = newUser;
                }

                session.setAttribute("userPhone",user.getId());
                return Result.success("");
            }
        } else {
            return Result.error("验证码不正确");
        }

        return Result.error("验证码不正确");
    }
}
