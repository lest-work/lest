package com.lest.modules.auth.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.auth.entity.dto.DictionaryDataDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器（字段名严格对齐数据库 sys_dictionary_data，无 alias）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/dictionary-data")
@RequiredArgsConstructor
public class DictionaryDataController {

    private final DictionaryService dictionaryService;

    /** 分页查询字典数据 */
    @GetMapping("/page")
    public Result<PageResult<DictionaryDataVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long dictId,
            @RequestParam(required = false) String dictCode) {
        return Result.ok(dictionaryService.pageData(page, size, dictId, dictCode));
    }

    /** 查询字典数据列表（统一返回 PageResult 格式） */
    @GetMapping
    public Result<PageResult<DictionaryDataVO>> list(
            @RequestParam(required = false) Long dictId,
            @RequestParam(required = false) String dictCode) {
        List<DictionaryDataVO> data;
        if (dictId != null) {
            data = dictionaryService.getDataByDictId(dictId);
        } else if (dictCode != null && !dictCode.isEmpty()) {
            data = dictionaryService.getDataByDictCode(dictCode);
        } else {
            data = List.of();
        }
        return Result.ok(PageResult.of(data));
    }

    /** 根据ID查询字典数据 */
    @GetMapping("/{id}")
    public Result<DictionaryDataVO> getById(@PathVariable Long id) {
        return Result.ok(dictionaryService.getDataById(id));
    }

    /** 创建字典数据 */
    @PostMapping
    public Result<Long> create(@RequestBody DictionaryDataDTO dto) {
        log.info("创建字典数据: dictId={}, dataKey={}", dto.getDictId(), dto.getDataKey());
        String dataValue = (dto.getDataValue() != null && !dto.getDataValue().isEmpty())
                ? dto.getDataValue() : dto.getDataKey();
        return Result.ok(dictionaryService.createData(
                dto.getDictId(), dto.getDataKey(), dataValue, dto.getLabel(),
                dto.getSort(), dto.getStatus()));
    }

    /** 修改字典数据 */
    @PutMapping
    public Result<Void> update(@RequestBody DictionaryDataDTO dto) {
        log.info("修改字典数据: id={}", dto.getId());
        String dataValue = (dto.getDataValue() != null && !dto.getDataValue().isEmpty())
                ? dto.getDataValue() : dto.getDataKey();
        dictionaryService.updateData(
                dto.getId(), dto.getDataKey(), dataValue, dto.getLabel(),
                dto.getSort(), dto.getStatus());
        return Result.ok();
    }

    /** 删除字典数据 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除字典数据: id={}", id);
        dictionaryService.deleteData(id);
        return Result.ok();
    }

    /** 批量删除字典数据 */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                dictionaryService.deleteData(id);
            }
        }
        return Result.ok();
    }
}
