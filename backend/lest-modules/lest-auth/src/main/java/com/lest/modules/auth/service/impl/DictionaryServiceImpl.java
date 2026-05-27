package com.lest.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.Assert;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.common.base.PageResult;
import com.lest.modules.auth.entity.domain.SysDictionary;
import com.lest.modules.auth.entity.domain.SysDictionaryData;
import com.lest.modules.auth.entity.dto.DictionaryDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.entity.vo.DictionaryVO;
import com.lest.modules.auth.mapper.SysDictionaryDataMapper;
import com.lest.modules.auth.mapper.SysDictionaryMapper;
import com.lest.modules.auth.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final SysDictionaryMapper dictionaryMapper;
    private final SysDictionaryDataMapper dictionaryDataMapper;

    @Override
    public PageResult<DictionaryVO> page(Integer page, Integer size, String dictCode, String dictName, Integer status) {
        Page<SysDictionary> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysDictionary> wrapper = new LambdaQueryWrapper<>();
        if (dictCode != null && !dictCode.isEmpty()) wrapper.like(SysDictionary::getDictCode, dictCode);
        if (dictName != null && !dictName.isEmpty()) wrapper.like(SysDictionary::getDictName, dictName);
        if (status != null) wrapper.eq(SysDictionary::getStatus, status);
        wrapper.orderByAsc(SysDictionary::getCreatedAt);
        Page<SysDictionary> result = dictionaryMapper.selectPage(pageParam, wrapper);
        List<DictionaryVO> records = result.getRecords().stream()
                .map(dict -> {
                    DictionaryVO vo = convertToVO(dict);
                    vo.setDataList(getDataList(dict.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public DictionaryVO getById(Long id) {
        SysDictionary dict = dictionaryMapper.selectById(id);
        Assert.notNull(dict, ErrorCode.DICT_NOT_FOUND);
        DictionaryVO vo = convertToVO(dict);
        vo.setDataList(getDataList(id));
        return vo;
    }

    @Override
    public DictionaryVO getByDictCode(String dictCode) {
        SysDictionary dict = dictionaryMapper.selectOne(
                new LambdaQueryWrapper<SysDictionary>()
                        .eq(SysDictionary::getDictCode, dictCode)
                        .eq(SysDictionary::getDeleted, 0));
        Assert.notNull(dict, ErrorCode.DICT_NOT_FOUND);
        DictionaryVO vo = convertToVO(dict);
        vo.setDataList(getDataList(dict.getId()));
        return vo;
    }

    @Override
    public List<DictionaryDataVO> getDataByDictCode(String dictCode) {
        SysDictionary dict = dictionaryMapper.selectOne(
                new LambdaQueryWrapper<SysDictionary>()
                        .eq(SysDictionary::getDictCode, dictCode)
                        .eq(SysDictionary::getDeleted, 0));
        if (dict == null) return List.of();
        return getDataList(dict.getId());
    }

    @Override
    public Long create(DictionaryDTO dto) {
        Assert.isNull(dictionaryMapper.selectOne(
                new LambdaQueryWrapper<SysDictionary>()
                        .eq(SysDictionary::getDictCode, dto.getDictCode())
                        .eq(SysDictionary::getDeleted, 0)),
                ErrorCode.DICT_CODE_EXISTS);

        SysDictionary dict = new SysDictionary();
        dict.setDictCode(dto.getDictCode());
        dict.setDictName(dto.getDictName());
        dict.setDescription(dto.getDescription());
        dict.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        dictionaryMapper.insert(dict);

        log.info("创建字典成功: dictId={}, dictCode={}", dict.getId(), dto.getDictCode());
        return dict.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictionaryDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.DICT_NOT_FOUND);
        SysDictionary dict = dictionaryMapper.selectById(dto.getId());
        Assert.notNull(dict, ErrorCode.DICT_NOT_FOUND);

        if (dto.getDictCode() != null && !dto.getDictCode().equals(dict.getDictCode())) {
            Assert.isNull(dictionaryMapper.selectOne(
                    new LambdaQueryWrapper<SysDictionary>()
                            .eq(SysDictionary::getDictCode, dto.getDictCode())
                            .eq(SysDictionary::getDeleted, 0)
                            .ne(SysDictionary::getId, dto.getId())),
                    ErrorCode.DICT_CODE_EXISTS);
        }

        SysDictionary updateDict = new SysDictionary();
        updateDict.setId(dto.getId());
        if (dto.getDictCode() != null) updateDict.setDictCode(dto.getDictCode());
        if (dto.getDictName() != null) updateDict.setDictName(dto.getDictName());
        if (dto.getDescription() != null) updateDict.setDescription(dto.getDescription());
        if (dto.getStatus() != null) updateDict.setStatus(dto.getStatus());

        dictionaryMapper.updateById(updateDict);

        log.info("更新字典成功: dictId={}", dto.getId());
    }

    @Override
    public void delete(Long id) {
        SysDictionary dict = dictionaryMapper.selectById(id);
        Assert.notNull(dict, ErrorCode.DICT_NOT_FOUND);
        dictionaryDataMapper.delete(
                new LambdaQueryWrapper<SysDictionaryData>().eq(SysDictionaryData::getDictId, id));
        dictionaryMapper.deleteById(id);
        log.info("删除字典成功: dictId={}", id);
    }

    @Override
    public DictionaryDataVO getDataById(Long id) {
        SysDictionaryData data = dictionaryDataMapper.selectById(id);
        Assert.notNull(data, ErrorCode.DICT_NOT_FOUND);
        return convertDataToVO(data);
    }

    @Override
    public Long createData(Long dictId, String dataKey, String dataValue,
                           String label, Integer sort, Integer status) {
        Assert.isNull(dictionaryDataMapper.selectOne(
                new LambdaQueryWrapper<SysDictionaryData>()
                        .eq(SysDictionaryData::getDictId, dictId)
                        .eq(SysDictionaryData::getDataKey, dataKey)
                        .eq(SysDictionaryData::getDeleted, 0)),
                ErrorCode.DICT_DATA_KEY_EXISTS);

        SysDictionaryData data = new SysDictionaryData();
        data.setDictId(dictId);
        data.setDataKey(dataKey);
        data.setDataValue(dataValue);
        data.setLabel(label);
        data.setSort(sort != null ? sort : 0);
        data.setStatus(status != null ? status : 1);

        dictionaryDataMapper.insert(data);

        log.info("创建字典数据成功: dataId={}, dataKey={}", data.getId(), dataKey);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(Long id, String dataKey, String dataValue,
                           String label, Integer sort, Integer status) {
        SysDictionaryData data = dictionaryDataMapper.selectById(id);
        Assert.notNull(data, ErrorCode.DICT_NOT_FOUND);

        if (dataKey != null && !dataKey.equals(data.getDataKey())) {
            Assert.isNull(dictionaryDataMapper.selectOne(
                    new LambdaQueryWrapper<SysDictionaryData>()
                            .eq(SysDictionaryData::getDictId, data.getDictId())
                            .eq(SysDictionaryData::getDataKey, dataKey)
                            .eq(SysDictionaryData::getDeleted, 0)
                            .ne(SysDictionaryData::getId, id)),
                    ErrorCode.DICT_DATA_KEY_EXISTS);
        }

        SysDictionaryData updateData = new SysDictionaryData();
        updateData.setId(id);
        if (dataKey != null) updateData.setDataKey(dataKey);
        if (dataValue != null) updateData.setDataValue(dataValue);
        if (label != null) updateData.setLabel(label);
        if (sort != null) updateData.setSort(sort);
        if (status != null) updateData.setStatus(status);

        dictionaryDataMapper.updateById(updateData);

        log.info("更新字典数据成功: dataId={}", id);
    }

    @Override
    public void deleteData(Long id) {
        dictionaryDataMapper.deleteById(id);
        log.info("删除字典数据成功: dataId={}", id);
    }

    /** 获取字典数据列表 */
    private List<DictionaryDataVO> getDataList(Long dictId) {
        LambdaQueryWrapper<SysDictionaryData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictionaryData::getDictId, dictId);
        wrapper.eq(SysDictionaryData::getDeleted, 0);
        wrapper.orderByAsc(SysDictionaryData::getSort);
        return dictionaryDataMapper.selectList(wrapper).stream()
                .map(this::convertDataToVO).collect(Collectors.toList());
    }

    /** 实体转VO */
    private DictionaryVO convertToVO(SysDictionary dict) {
        return DictionaryVO.builder()
                .id(dict.getId())
                .dictCode(dict.getDictCode())
                .dictName(dict.getDictName())
                .description(dict.getDescription())
                .status(dict.getStatus())
                .createdAt(dict.getCreatedAt())
                .updatedAt(dict.getUpdatedAt())
                .build();
    }

    /** 字典数据实体转VO */
    private DictionaryDataVO convertDataToVO(SysDictionaryData data) {
        return DictionaryDataVO.builder()
                .id(data.getId())
                .dictId(data.getDictId())
                .dataKey(data.getDataKey())
                .dataValue(data.getDataValue())
                .label(data.getLabel())
                .sort(data.getSort())
                .status(data.getStatus())
                .createdAt(data.getCreatedAt())
                .updatedAt(data.getUpdatedAt())
                .build();
    }
}
