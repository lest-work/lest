package com.lest.modules.system.service;

import com.lest.common.base.PageResult;
import com.lest.modules.system.entity.dto.BackupDTO;
import com.lest.modules.system.entity.vo.BackupVO;

import java.util.List;

/**
 * 数据备份服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface BackupService {

    /**
     * 分页查询备份记录
     *
     * @param page       页码
     * @param size       每页大小
     * @param status     状态
     * @param backupType 备份类型
     * @return 分页结果
     */
    PageResult<BackupVO> page(Integer page, Integer size, String status, String backupType);

    /**
     * 查询所有备份记录
     *
     * @return 备份列表
     */
    List<BackupVO> list();

    /**
     * 根据ID查询备份记录
     *
     * @param id 备份ID
     * @return 备份信息
     */
    BackupVO getById(Long id);

    /**
     * 创建备份记录
     *
     * @param dto 备份信息
     * @return 新建备份的ID
     */
    Long create(BackupDTO dto);

    /**
     * 触发备份任务
     *
     * @param backupType 备份类型（manual-手动，auto-自动）
     */
    void triggerBackup(String backupType);

    /**
     * 从备份恢复数据
     *
     * @param id 备份ID
     */
    void restore(Long id);

    /**
     * 删除备份记录
     *
     * @param id 备份ID
     */
    void delete(Long id);
}
