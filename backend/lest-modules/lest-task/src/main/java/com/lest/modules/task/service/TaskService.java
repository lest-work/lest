package com.lest.modules.task.service;

import com.lest.common.base.PageResult;
import com.lest.modules.task.entity.dto.*;
import com.lest.modules.task.entity.vo.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务服务接口
 *
 * @author Lest
 * @since 2026-05-26
 */
public interface TaskService {

    /**
     * 创建任务
     */
    Long create(TaskDTO dto);

    /**
     * 更新任务
     */
    void update(TaskDTO dto);

    /**
     * 删除任务
     */
    void delete(Long id);

    /**
     * 获取任务详情
     */
    TaskVO getById(Long id);

    /**
     * 分页查询任务
     */
    PageResult<TaskVO> page(Long projectId, Long iterationId, Long assigneeId, String status,
                            String priority, List<Long> labels, String keyword, Integer page, Integer size);

    /**
     * 更新任务状态
     */
    void updateStatus(Long id, String status);

    /**
     * 分配任务
     */
    void assign(Long id, AssignDTO dto);

    /**
     * 认领任务
     */
    void claim(Long id);

    /**
     * 获取看板视图
     */
    List<BoardVO> getBoard(Long projectId, Long iterationId);

    /**
     * 看板拖拽移动
     */
    void move(Long id, MoveDTO dto);

    /**
     * 获取甘特图数据
     */
    List<GanttVO> getGantt(Long projectId, Long iterationId, LocalDate startDate, LocalDate endDate);

    /**
     * 添加子任务
     */
    Long createSubtask(Long parentId, TaskDTO dto);

    /**
     * 获取子任务列表
     */
    List<TaskVO> getSubtasks(Long parentId);

    /**
     * 添加依赖
     */
    void addDependency(Long id, DependencyDTO dto);

    /**
     * 获取依赖列表
     */
    List<TaskDependencyVO> getDependencies(Long id);

    /**
     * 删除依赖
     */
    void deleteDependency(Long id, Long dependencyTaskId);

    /**
     * 添加工时记录
     */
    Long addWorklog(Long id, WorklogDTO dto);

    /**
     * 获取工时记录
     */
    PageResult<WorklogVO> getWorklogs(Long id, Integer page, Integer size);

    /**
     * 获取关联的提交
     */
    List<CommitVO> getCommits(Long id);

    /**
     * 获取关联的MR
     */
    List<MergeRequestVO> getMergeRequests(Long id);

    /**
     * 手动关联提交
     */
    void addCommit(Long id, CommitDTO dto);

    /**
     * 手动关联MR
     */
    void addMergeRequest(Long id, MergeRequestDTO dto);
}
