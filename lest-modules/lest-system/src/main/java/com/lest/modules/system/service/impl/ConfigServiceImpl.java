package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.domain.SysConfig;
import com.lest.modules.system.entity.dto.ConfigDTO;
import com.lest.modules.system.entity.vo.ConfigVO;
import com.lest.modules.system.mapper.SysConfigMapper;
import com.lest.modules.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public PageResult<ConfigVO> page(Integer page, Integer size, String configGroup, String configKey, Integer status) {
        Page<SysConfig> p = new Page<>(page, size);
        LambdaQueryWrapper<SysConfig> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(configGroup)) w.eq(SysConfig::getConfigGroup, configGroup);
        if (StringUtils.hasText(configKey)) w.like(SysConfig::getConfigKey, configKey);
        if (status != null) w.eq(SysConfig::getStatus, status);
        w.orderByAsc(SysConfig::getSort);
        IPage<SysConfig> result = configMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), page, size);
    }

    @Override
    public List<ConfigVO> list(String configGroup) {
        LambdaQueryWrapper<SysConfig> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(configGroup)) w.eq(SysConfig::getConfigGroup, configGroup);
        w.orderByAsc(SysConfig::getSort);
        return configMapper.selectList(w).stream().map(this::toVO).toList();
    }

    @Override
    public ConfigVO getById(Long id) {
        return toVO(configMapper.selectById(id));
    }

    @Override
    public ConfigVO getByKey(String configKey) {
        return toVO(configMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey)));
    }

    @Override
    public Long create(ConfigDTO dto) {
        SysConfig entity = toEntity(dto);
        configMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void update(ConfigDTO dto) {
        SysConfig entity = toEntity(dto);
        configMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        configMapper.deleteById(id);
    }

    /**
     * 根据分组查询所有启用的配置，以Map形式返回，便于前端直接使用
     */
    @Override
    public Map<String, String> getByGroup(String configGroup) {
        return configMapper.selectList(
            new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigGroup, configGroup)
                .eq(SysConfig::getStatus, 1)
        ).stream().collect(Collectors.toMap(SysConfig::getConfigKey, SysConfig::getConfigValue));
    }

    @Override
    public String getValue(String configKey, String defaultValue) {
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey));
        return config != null ? config.getConfigValue() : defaultValue;
    }

    /** 实体转VO */
    private ConfigVO toVO(SysConfig e) {
        if (e == null) return null;
        return new ConfigVO(e.getId(), e.getConfigKey(), e.getConfigValue(), e.getConfigType(),
            e.getConfigGroup(), e.getDescription(), e.getIsSystem(), e.getSort(), e.getStatus(),
            e.getCreatedAt(), e.getUpdatedAt());
    }

    /** DTO转实体，null值使用默认值 */
    private SysConfig toEntity(ConfigDTO dto) {
        SysConfig e = new SysConfig();
        if (dto.id() != null) e.setId(dto.id());
        e.setConfigKey(dto.configKey());
        e.setConfigValue(dto.configValue());
        e.setConfigType(dto.configType() != null ? dto.configType() : "string");
        e.setConfigGroup(dto.configGroup());
        e.setDescription(dto.description());
        e.setIsSystem(dto.isSystem() != null ? dto.isSystem() : 0);
        e.setSort(dto.sort() != null ? dto.sort() : 0);
        e.setStatus(dto.status() != null ? dto.status() : 1);
        return e;
    }
}
