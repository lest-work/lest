package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.domain.SysBackup;
import com.lest.modules.system.entity.dto.BackupDTO;
import com.lest.modules.system.entity.vo.BackupVO;
import com.lest.modules.system.mapper.SysBackupMapper;
import com.lest.modules.system.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据备份服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

    private final SysBackupMapper backupMapper;

    @Override
    public PageResult<BackupVO> page(Integer page, Integer size, String status, String backupType) {
        Page<SysBackup> p = new Page<>(page != null ? page : 1, size != null ? size : 20);
        LambdaQueryWrapper<SysBackup> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) w.eq(SysBackup::getStatus, status);
        if (StringUtils.hasText(backupType)) w.eq(SysBackup::getBackupType, backupType);
        w.orderByDesc(SysBackup::getCreatedAt);
        IPage<SysBackup> result = backupMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), page, size);
    }

    @Override
    public List<BackupVO> list() {
        return backupMapper.selectList(
            new LambdaQueryWrapper<SysBackup>()
                .orderByDesc(SysBackup::getCreatedAt)
        ).stream().map(this::toVO).toList();
    }

    @Override
    public BackupVO getById(Long id) {
        return toVO(backupMapper.selectById(id));
    }

    @Override
    public Long create(BackupDTO dto) {
        SysBackup entity = toEntity(dto);
        entity.setStatus("pending");
        backupMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 触发备份任务
     * 实际生产环境中此处应调用数据库备份工具（如mysqldump）或调用OSS等对象存储API
     */
    @Override
    public void triggerBackup(String backupType) {
        SysBackup backup = new SysBackup();
        backup.setBackupName("手动备份-" + LocalDateTime.now());
        backup.setBackupType(StringUtils.hasText(backupType) ? backupType : "manual");
        backup.setStatus("running");
        backup.setCreatedAt(LocalDateTime.now());
        backupMapper.insert(backup);
        log.info("触发备份任务: {}", backup.getId());
    }

    /**
     * 从备份恢复数据
     * 实际生产环境中此处应执行数据库恢复操作
     */
    @Override
    public void restore(Long id) {
        log.info("从备份 {} 恢复数据（预留接口）", id);
    }

    @Override
    public void delete(Long id) {
        backupMapper.deleteById(id);
    }

    /** 实体转VO */
    private BackupVO toVO(SysBackup e) {
        if (e == null) return null;
        return new BackupVO(e.getId(), e.getBackupName(), e.getBackupType(),
            e.getBackupPath(), e.getFileSize(), e.getStatus(), e.getDescription(),
            e.getCreatedBy(), e.getCompletedAt(), e.getCreatedAt());
    }

    /** DTO转实体，null值使用默认值 */
    private SysBackup toEntity(BackupDTO dto) {
        SysBackup e = new SysBackup();
        if (dto.id() != null) e.setId(dto.id());
        e.setBackupName(dto.backupName());
        e.setBackupType(dto.backupType() != null ? dto.backupType() : "manual");
        e.setDescription(dto.description());
        return e;
    }
}
