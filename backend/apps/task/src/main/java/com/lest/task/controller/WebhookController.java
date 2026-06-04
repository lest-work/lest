package com.lest.task.controller;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.task.domain.TaskCommit;
import com.lest.task.service.IWebhookService;

/**
 * Webhook回调
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private static final String SIG_HEADER = "X-Signature";
    private static final String SIG_ALGO = "HmacSHA256";

    @Value("${webhook.secret:default-secret-change-me}")
    private String webhookSecret;

    @Autowired
    private IWebhookService webhookService;

    private boolean verifySignature(String payload, String signature)
    {
        try
        {
            Mac mac = Mac.getInstance(SIG_ALGO);
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), SIG_ALGO);
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hash);
            return computed.equalsIgnoreCase(signature);
        }
        catch (Exception e)
        {
            log.error("签名验证失败", e);
            return false;
        }
    }

    @PostMapping("/ci/build")
    public AjaxResult ciBuild(
            @RequestHeader(value = SIG_HEADER, required = false) String signature,
            @RequestBody String payload)
    {
        if (signature != null && !verifySignature(payload, signature))
        {
            return error("签名验证失败");
        }
        webhookService.handleCIBuild(null);
        return success();
    }

    @PostMapping("/git/commit")
    public AjaxResult gitCommit(
            @RequestHeader(value = SIG_HEADER, required = false) String signature,
            @RequestBody String payload)
    {
        if (signature != null && !verifySignature(payload, signature))
        {
            return error("签名验证失败");
        }
        webhookService.handleGitCommit(null);
        return success();
    }
}
