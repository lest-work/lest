package com.lest.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.auth.entity.domain.AuthCaptcha;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 验证码Mapper接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Mapper
public interface AuthCaptchaMapper extends BaseMapper<AuthCaptcha> {

    /**
     * 删除过期的验证码
     *
     * @return 删除数量
     */
    int deleteExpiredCaptchas();
}
