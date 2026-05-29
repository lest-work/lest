package com.lest.system.api.factory;

import com.lest.common.core.web.domain.R;
import com.lest.system.api.RemoteFileService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务降级工厂
 *
 * @author yshan2028
 */
@Slf4j
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {

    @Override
    public RemoteFileService create(Throwable cause) {
        log.error("RemoteFileService 调用失败: {}", cause.getMessage());
        return new RemoteFileService() {
            @Override
            public R<String> upload(MultipartFile file, String source) {
                return R.fail("文件上传失败: " + cause.getMessage());
            }

            @Override
            public R<Boolean> delete(String fileUrl, String source) {
                return R.fail("文件删除失败: " + cause.getMessage());
            }
        };
    }
}
