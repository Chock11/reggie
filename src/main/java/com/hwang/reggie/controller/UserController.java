package com.hwang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwang.reggie.common.R;
import com.hwang.reggie.entity.User;
import com.hwang.reggie.service.UserService;
import com.hwang.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 发送手机短信验证码
    * */

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //获取手机号

        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成四位的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("生成的验证码是:{}",code);
            //调用阿里云提供的短信服务API发送端信服务
            //SMSUtils.sendMessage("瑞吉外卖","你的阿里云模板",phone,code)
            //将验证码把存起来到Session中。
            httpSession.setAttribute(phone,code);

            //降生陈大哥验证码放入到redis当中，并设置有效期为5分钟。
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功");
        }

        return R.success("手机验证码发送失败");

    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession){
        //获取手机号
        log.info(map.toString());
        //获取验证码
        String phone = map.get("phone").toString();
        String code =  map.get("phone").toString();
        //从Session当中获取保存的验证码
        Object codeInSession = httpSession.getAttribute("phone");

        //从redis中获取保存的验证码
        redisTemplate.opsForValue().get(phone);
        if (httpSession.getAttribute("phone")!= null &&  codeInSession.equals(code)){

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            if (user == null){
                //说明之前没这个用户，自动完成注册
                user =new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());

            //如果用户登陆成功那么删除redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");

    }
}
