package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.system.entity.dto.PostDTO;
import com.lest.modules.system.entity.dto.PostQueryDTO;
import com.lest.modules.system.entity.vo.PostVO;
import com.lest.modules.system.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    public Result<PageResult<PostVO>> list(PostQueryDTO dto) {
        return Result.ok(postService.page(dto));
    }

    @GetMapping("/{id}")
    public Result<PostVO> getById(@PathVariable Long id) {
        return Result.ok(postService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody PostDTO dto) {
        postService.add(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody PostDTO dto) {
        postService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return Result.ok();
    }
}
