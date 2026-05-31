package com.lest.gateway.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import static com.google.code.kaptcha.Constants.*;

/**
 * 验证码配置
 *
 * @author ruoyi
 */
@Configuration
public class CaptchaConfig
{
    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框
        properties.setProperty(KAPTCHA_BORDER, "no");
        // 验证码文本字符颜色 - 深色高对比度
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "20,20,20");
        // 验证码图片宽度
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "120");
        // 验证码图片高度
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "40");
        // 验证码文本字符大小
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "32");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 验证码文本字符长度
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 验证码文本字体样式
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Verdana");
        // 干扰噪点颜色
        properties.setProperty(KAPTCHA_NOISE_COLOR, "100,100,100");
        // 干扰实现类 - 无干扰（干扰线由 SimpleLineNoise 处理）
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 背景实现类 - 自定义干扰线背景
        defaultKaptcha.setConfig(new Config(properties)
        {
            @Override
            public BackgroundProducer getBackgroundImpl()
            {
                return new SimpleLineNoise();
            }
        });
        return defaultKaptcha;
    }

    @Bean(name = "captchaProducerMath")
    public DefaultKaptcha getKaptchaBeanMath()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框
        properties.setProperty(KAPTCHA_BORDER, "no");
        // 验证码文本字符颜色 - 深色
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "20,20,20");
        // 验证码图片宽度
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "120");
        // 验证码图片高度
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "40");
        // 验证码文本字符大小
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "30");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        // 验证码文本生成器
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.lest.gateway.config.KaptchaTextCreator");
        // 验证码文本字符间距
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "4");
        // 验证码文本字符长度
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 验证码文本字体样式
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Verdana");
        // 干扰噪点颜色
        properties.setProperty(KAPTCHA_NOISE_COLOR, "100,100,100");
        // 干扰实现类 - 无干扰
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 背景实现类 - 自定义干扰线背景
        defaultKaptcha.setConfig(new Config(properties)
        {
            @Override
            public BackgroundProducer getBackgroundImpl()
            {
                return new SimpleLineNoise();
            }
        });
        return defaultKaptcha;
    }
}
