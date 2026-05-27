package com.lest.modules.auth.controller;

import com.lest.common.base.PageResult;
import com.lest.common.base.Result;
import com.lest.modules.auth.entity.dto.DictionaryDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.entity.vo.DictionaryVO;
import com.lest.modules.auth.service.DictionaryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    /**
     * 分页查询字典
     *
     * @param page      页码
     * @param size      每页大小
     * @param dictCode  字典编码（模糊搜索）
     * @param dictName  字典名称（模糊搜索）
     * @param status    状态
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<DictionaryVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String dictCode,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) Integer status) {
        return Result.ok(dictionaryService.page(page, size, dictCode, dictName, status));
    }

    /**
     * 根据字典编码获取字典数据
     *
     * @param dictCode 字典编码
     * @return 字典数据列表
     */
    @GetMapping("/data/{dictCode}")
    public Result<List<DictionaryDataVO>> getDataByDictCode(@PathVariable String dictCode) {
        return Result.ok(dictionaryService.getDataByDictCode(dictCode));
    }

    /**
     * 根据ID查询字典
     *
     * @param id 字典ID
     * @return 字典VO
     */
    @GetMapping("/{id}")
    public Result<DictionaryVO> getById(@PathVariable Long id) {
        return Result.ok(dictionaryService.getById(id));
    }

    /**
     * 根据字典编码查询字典
     *
     * @param dictCode 字典编码
     * @return 字典VO
     */
    @GetMapping("/code/{dictCode}")
    public Result<DictionaryVO> getByDictCode(@PathVariable String dictCode) {
        return Result.ok(dictionaryService.getByDictCode(dictCode));
    }

    /**
     * 创建字典
     *
     * @param dto 字典DTO
     * @return 字典ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody DictionaryDTO dto) {
        log.info("创建字典: dictCode={}", dto.getDictCode());
        return Result.ok(dictionaryService.create(dto));
    }

    /**
     * 更新字典
     *
     * @param dto 字典DTO
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody DictionaryDTO dto) {
        log.info("更新字典: dictId={}", dto.getId());
        dictionaryService.update(dto);
        return Result.ok();
    }

    /**
     * 删除字典
     *
     * @param id 字典ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除字典: dictId={}", id);
        dictionaryService.delete(id);
        return Result.ok();
    }

    /**
     * 创建字典数据
     *
     * @param dictId   字典ID
     * @param dataKey  数据键
     * @param dataValue 数据值
     * @param label    标签
     * @param sort     排序
     * @param status   状态
     * @return 字典数据ID
     */
    @PostMapping("/data")
    public Result<Long> createData(
            @RequestParam Long dictId,
            @RequestParam String dataKey,
            @RequestParam String dataValue,
            @RequestParam String label,
            @RequestParam(required = false) Integer sort,
            @RequestParam(required = false) Integer status) {
        log.info("创建字典数据: dictId={}, dataKey={}", dictId, dataKey);
        return Result.ok(dictionaryService.createData(dictId, dataKey, dataValue, label, sort, status));
    }

    /**
     * 更新字典数据
     *
     * @param id       字典数据ID
     * @param dataKey  数据键
     * @param dataValue 数据值
     * @param label    标签
     * @param sort     排序
     * @param status   状态
     * @return 操作结果
     */
    @PutMapping("/data/{id}")
    public Result<Void> updateData(
            @PathVariable Long id,
            @RequestParam(required = false) String dataKey,
            @RequestParam(required = false) String dataValue,
            @RequestParam(required = false) String label,
            @RequestParam(required = false) Integer sort,
            @RequestParam(required = false) Integer status) {
        log.info("更新字典数据: dataId={}", id);
        dictionaryService.updateData(id, dataKey, dataValue, label, sort, status);
        return Result.ok();
    }

    /**
     * 删除字典数据
     *
     * @param id 字典数据ID
     * @return 操作结果
     */
    @DeleteMapping("/data/{id}")
    public Result<Void> deleteData(@PathVariable Long id) {
        log.info("删除字典数据: dataId={}", id);
        dictionaryService.deleteData(id);
        return Result.ok();
    }
}
