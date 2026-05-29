package com.lest.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.common.security.util.LoginUser;
import com.lest.common.security.util.SecurityUtils;
import com.lest.modules.file.entity.domain.FileRecord;
import com.lest.modules.file.entity.vo.FileRecordVO;
import com.lest.modules.file.mapper.FileRecordMapper;
import com.lest.modules.file.service.FileRecordService;
import com.lest.modules.file.service.impl.MinioFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileRecordServiceImpl implements FileRecordService {

    private final FileRecordMapper fileRecordMapper;
    private final MinioFileStorageService minioFileStorageService;

    @Value("${minio.public-url:http://127.0.0.1:9000}")
    private String publicUrl;

    @Value("${minio.bucket:lest-files}")
    private String bucket;

    private FileRecord buildRecord(String name, String objectName, long length, String contentType) {
        FileRecord record = new FileRecord();
        record.setName(name);
        record.setPath(objectName);
        record.setLength(length);
        record.setContentType(contentType);
        record.setCreatedAt(LocalDateTime.now());

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            record.setCreateUserId(loginUser.getUserId());
            record.setCreateUsername(loginUser.getUsername());
            record.setCreateNickname(loginUser.getNickname());
        }
        return record;
    }

    @Override
    public FileRecordVO upload(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = "files/" + UUID.randomUUID().toString().replace("-", "") + ext;

            minioFileStorageService.upload(objectName, file.getInputStream(), file.getSize(), file.getContentType());

            FileRecord record = buildRecord(originalFilename, objectName, file.getSize(), file.getContentType());
            fileRecordMapper.insert(record);

            return toVO(record);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileRecordVO uploadBase64(String base64Data, String fileName) {
        try {
            String data = base64Data;
            if (base64Data.contains(",")) {
                data = base64Data.substring(base64Data.indexOf(",") + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(data);
            String ext = "";
            if (fileName != null && fileName.contains(".")) {
                ext = fileName.substring(fileName.lastIndexOf("."));
            }
            String objectName = "files/" + UUID.randomUUID().toString().replace("-", "") + ext;

            minioFileStorageService.upload(objectName, new ByteArrayInputStream(bytes), bytes.length, null);

            FileRecord record = buildRecord(
                    fileName != null ? fileName : "upload",
                    objectName,
                    bytes.length,
                    null);
            fileRecordMapper.insert(record);

            return toVO(record);
        } catch (Exception e) {
            log.error("Base64文件上传失败", e);
            throw new RuntimeException("Base64文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<FileRecordVO> list(FileRecord query) {
        LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getCreateUserId() != null, FileRecord::getCreateUserId, query.getCreateUserId());
        wrapper.like(query.getName() != null, FileRecord::getName, query.getName());
        wrapper.like(query.getPath() != null, FileRecord::getPath, query.getPath());
        wrapper.like(query.getCreateNickname() != null, FileRecord::getCreateNickname, query.getCreateNickname());
        wrapper.orderByDesc(FileRecord::getCreatedAt);
        return fileRecordMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public List<FileRecordVO> page(FileRecord query) {
        return list(query);
    }

    @Override
    public FileRecordVO getById(Long id) {
        FileRecord record = fileRecordMapper.selectById(id);
        return record != null ? toVO(record) : null;
    }

    @Override
    public void remove(Long id) {
        FileRecord record = fileRecordMapper.selectById(id);
        if (record != null) {
            try {
                minioFileStorageService.delete(record.getPath());
            } catch (Exception e) {
                log.warn("MinIO文件删除失败, path={}", record.getPath(), e);
            }
            fileRecordMapper.deleteById(id);
        }
    }

    @Override
    public void removeBatch(List<Long> ids) {
        for (Long id : ids) {
            remove(id);
        }
    }

    @Override
    public InputStream download(Long id) {
        FileRecord record = fileRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("文件不存在");
        }
        return minioFileStorageService.get(record.getPath());
    }

    private FileRecordVO toVO(FileRecord record) {
        String url = publicUrl + "/" + bucket + "/" + record.getPath();
        String downloadUrl = publicUrl + "/" + bucket + "/" + record.getPath() 
                + "?download=true";
        return FileRecordVO.builder()
                .id(record.getId())
                .name(record.getName())
                .path(record.getPath())
                .length(record.getLength())
                .contentType(record.getContentType())
                .createUserId(record.getCreateUserId())
                .createUsername(record.getCreateUsername())
                .createNickname(record.getCreateNickname())
                .createdAt(record.getCreatedAt())
                .url(url)
                .thumbnail(url)
                .downloadUrl(downloadUrl)
                .build();
    }
}
