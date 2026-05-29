package com.lest.modules.system.service;

import com.lest.modules.system.entity.dto.DeptDTO;
import com.lest.modules.system.entity.vo.DeptVO;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author yshan2028
 */
public interface DeptService {

    /**
     * 获取部门树
     */
    List<DeptVO> selectDeptTree();

    /**
     * 获取部门列表
     */
    List<DeptVO> selectDeptList(String deptName, String deptCode, Integer status);

    /**
     * 根据ID获取部门
     */
    DeptVO getById(Long deptId);

    /**
     * 新增部门
     */
    Long createDept(DeptDTO dto);

    /**
     * 修改部门
     */
    void updateDept(DeptDTO dto);

    /**
     * 删除部门
     */
    void deleteDept(Long deptId);

    /**
     * 获取角色的部门ID列表
     */
    List<Long> getDeptIdsByRoleId(Long roleId);

    /**
     * 构建角色部门树（带选中状态）
     */
    List<DeptVO> buildRoleDeptTree(List<DeptVO> allDepts, List<Long> checkedDeptIds);

    /**
     * 过滤排除指定部门及其子部门
     */
    List<DeptVO> filterExcludeDept(List<DeptVO> depts, Long excludeDeptId);
}
