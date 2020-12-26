package com.imooc.diners.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.utils.AssertUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 发送短信验证码service
 *
 * @author pengfei.zhao
 * @date 2020/12/26 9:32
 */
@Service
public class SendVerifyCodeService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void send(String phone){
        // 检查非空, 手机号格式验证
        AssertUtil.isNotEmpty(phone, "手机号不能为空");

        // 检查验证码是否发过
        if (!checkCodeIsExpired(phone)) {
            return;
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        // 发送验证码
        redisTemplate.opsForValue().set(RedisKeyConstant.verify_code.getKey() + phone,
                code, 60, TimeUnit.SECONDS);
    }

    private boolean checkCodeIsExpired(String phone) {
        String code = redisTemplate.opsForValue().get(RedisKeyConstant.verify_code.getKey() + phone);
        return StrUtil.isBlank(code)? true: false;
    }

    /**
     * 根据手机号获取验证码
     *
     * @param phone
     * @return
     */
    public String getCodeByPhone(String phone) {
        String key = RedisKeyConstant.verify_code.getKey() + phone;
        return redisTemplate.opsForValue().get(key);
    }
}
