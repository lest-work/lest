package com.lest.modules.auth.service;

import com.lest.common.base.PageResult;
import com.lest.modules.auth.entity.dto.DictionaryDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.entity.vo.DictionaryVO;

import java.util.List;

/**
 * 字典服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface DictionaryService {

    PageResult<DictionaryVO> page(Integer page, Integer size, String dictCode, String dictName, Integer status);
    DictionaryVO getById(Long id);
    DictionaryVO getByDictCode(String dictCode);
    List<DictionaryDataVO> getDataByDictCode(String dictCode);
    Long create(DictionaryDTO dto);
    void update(DictionaryDTO dto);
    void delete(Long id);
    DictionaryDataVO getDataById(Long id);
    Long createData(Long dictId, String dataKey, String dataValue, String label, Integer sort, Integer status);
    void updateData(Long id, String dataKey, String dataValue, String label, Integer sort, Integer status);
    void deleteData(Long id);
}
