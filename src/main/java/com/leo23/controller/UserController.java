package com.leo23.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leo23.common.R;
import com.leo23.entity.User;
import com.leo23.service.UserService;
import com.leo23.utils.SMSUtils;
import com.leo23.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码: {}", code);
            // 调用阿里云提供的短信服务
//            SMSUtils.sendMessage("", "", phone, code);

            // 需要将生成的验证码保存到Session
            session.setAttribute("phone", phone);
            session.setAttribute("code", code);
            return R.success("手机短信验证码发送成功");
        }
        return R.error("手机短信验证码发送失败");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从session 中获取保存的验证码
        Object sessionCode = session.getAttribute("code");
        // 对比验证码
        if (sessionCode != null && sessionCode.equals(code)) {
            // 对比成功
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                // 新用户则自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
