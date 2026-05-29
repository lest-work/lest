package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.factory.RemoteFileFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteFileService", value = "lest-file", fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService {

    /**
     * 上传文件
     */
    @PostMapping(value = "/file/upload", consumes = "multipart/form-data")
    R<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 删除文件
     */
    @PostMapping("/file/delete")
    R<Boolean> delete(
            @RequestParam("fileUrl") String fileUrl,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
