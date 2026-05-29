package com.lest.modules.system.service;

import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.dto.ConfigDTO;
import com.lest.modules.system.entity.vo.ConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface ConfigService {

    /**
     * 分页查询系统配置
     *
     * @param page        页码
     * @param size        每页大小
     * @param configGroup 配置分组
     * @param configKey   配置键（模糊匹配）
     * @param status      状态
     * @return 分页结果
     */
    PageResult<ConfigVO> page(Integer page, Integer size, String configGroup, String configKey, Integer status);

    /**
     * 查询配置列表（不分页）
     *
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<ConfigVO> list(String configGroup);

    /**
     * 根据ID查询配置
     *
     * @param id 配置ID
     * @return 配置信息
     */
    ConfigVO getById(Long id);

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    ConfigVO getByKey(String configKey);

    /**
     * 创建配置
     *
     * @param dto 配置信息
     * @return 新建配置的ID
     */
    Long create(ConfigDTO dto);

    /**
     * 更新配置
     *
     * @param dto 配置信息
     */
    void update(ConfigDTO dto);

    /**
     * 删除配置
     *
     * @param id 配置ID
     */
    void delete(Long id);

    /**
     * 根据分组查询所有启用的配置，返回键值对
     *
     * @param configGroup 配置分组
     * @return 配置键值对
     */
    Map<String, String> getByGroup(String configGroup);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值（配置不存在时返回）
     * @return 配置值
     */
    String getValue(String configKey, String defaultValue);
}
