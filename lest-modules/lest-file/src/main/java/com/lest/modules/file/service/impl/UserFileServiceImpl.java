package com.lest.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.modules.file.entity.domain.UserFile;
import com.lest.modules.file.entity.dto.UserFileDTO;
import com.lest.modules.file.entity.vo.UserFileVO;
import com.lest.modules.file.mapper.UserFileMapper;
import com.lest.modules.file.service.UserFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFileServiceImpl implements UserFileService {

    private final UserFileMapper userFileMapper;

    @Value("${minio.public-url:http://127.0.0.1:9000}")
    private String publicUrl;

    @Value("${minio.bucket:lest-files}")
    private String bucket;

    @Override
    public List<UserFileVO> list(UserFile query) {
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getUserId() != null, UserFile::getUserId, query.getUserId());
        wrapper.eq(query.getIsDirectory() != null, UserFile::getIsDirectory, query.getIsDirectory());
        wrapper.eq(query.getParentId() != null, UserFile::getParentId, query.getParentId());
        wrapper.like(query.getName() != null, UserFile::getName, query.getName());
        wrapper.orderByAsc(UserFile::getIsDirectory)
               .orderByDesc(UserFile::getCreatedAt);
        return userFileMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public List<UserFileVO> page(UserFile query) {
        return list(query);
    }

    @Override
    public void add(UserFileDTO dto) {
        UserFile record = new UserFile();
        record.setUserId(dto.getUserId());
        record.setName(dto.getName());
        record.setIsDirectory(dto.getIsDirectory());
        record.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        record.setPath(dto.getPath());
        record.setLength(dto.getLength());
        record.setContentType(dto.getContentType());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        userFileMapper.insert(record);
    }

    @Override
    public void update(UserFileDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        UserFile record = new UserFile();
        record.setId(dto.getId());
        if (dto.getName() != null) {
            record.setName(dto.getName());
        }
        if (dto.getParentId() != null) {
            record.setParentId(dto.getParentId());
        }
        record.setUpdatedAt(LocalDateTime.now());
        userFileMapper.updateById(record);
    }

    @Override
    public void remove(Long id, Long userId) {
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFile::getId, id).eq(UserFile::getUserId, userId);
        userFileMapper.delete(wrapper);
    }

    @Override
    public void removeBatch(List<Long> ids, Long userId) {
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserFile::getId, ids).eq(UserFile::getUserId, userId);
        userFileMapper.delete(wrapper);
    }

    private UserFileVO toVO(UserFile record) {
        String url = null;
        String thumbnail = null;
        String downloadUrl = null;
        if (record.getPath() != null && !record.getPath().isEmpty()) {
            url = publicUrl + "/" + bucket + "/" + record.getPath();
            downloadUrl = url + "?download=true";
            if (record.getContentType() != null && record.getContentType().startsWith("image/")) {
                thumbnail = url;
            }
        }
        return UserFileVO.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .name(record.getName())
                .isDirectory(record.getIsDirectory())
                .parentId(record.getParentId())
                .path(record.getPath())
                .length(record.getLength())
                .contentType(record.getContentType())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .url(url)
                .thumbnail(thumbnail)
                .downloadUrl(downloadUrl)
                .build();
    }
}
