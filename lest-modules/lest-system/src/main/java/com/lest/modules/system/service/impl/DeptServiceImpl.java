package com.lest.modules.system.service.impl;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.modules.system.entity.dto.DeptDTO;
import com.lest.modules.system.entity.vo.DeptVO;
import com.lest.modules.system.service.DeptService;
import com.lest.system.api.RemoteDeptService;
import com.lest.system.api.domain.SysDept;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现（通过 Feign 调用 lest-auth）
 *
 * @author yshan2028
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private static final String SOURCE = "system";

    private final RemoteDeptService remoteDeptService;

    @Override
    public List<DeptVO> selectDeptTree() {
        R<List<SysDept>> result = remoteDeptService.getTree(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeptVO> selectDeptList(String deptName, String deptCode, Integer status) {
        R<List<SysDept>> result = remoteDeptService.listAll(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .filter(dept -> (deptName == null || deptName.isEmpty() ||
                        (dept.getOrgCode() != null && dept.getOrgCode().contains(deptName)) ||
                        (dept.getDeptName() != null && dept.getDeptName().contains(deptName))) &&
                        (deptCode == null || deptCode.isEmpty() ||
                                (dept.getOrgCode() != null && dept.getOrgCode().contains(deptCode))))
                .filter(dept -> status == null || dept.getStatus() != null &&
                        dept.getStatus().equals(status))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DeptVO getById(Long deptId) {
        R<SysDept> result = remoteDeptService.getById(deptId, SOURCE);
        if (result.getData() == null) {
            return null;
        }
        return convertToVO(result.getData());
    }

    @Override
    public Long createDept(DeptDTO dto) {
        SysDept dept = convertToSysDept(dto);
        R<Long> result = remoteDeptService.create(dept, SOURCE);
        return result.getData();
    }

    @Override
    public void updateDept(DeptDTO dto) {
        SysDept dept = convertToSysDept(dto);
        dept.setDeptId(dto.getId());
        remoteDeptService.update(dept, SOURCE);
    }

    @Override
    public void deleteDept(Long deptId) {
        remoteDeptService.delete(deptId, SOURCE);
    }

    @Override
    public List<Long> getDeptIdsByRoleId(Long roleId) {
        return List.of();
    }

    @Override
    public List<DeptVO> buildRoleDeptTree(List<DeptVO> allDepts, List<Long> checkedDeptIds) {
        if (checkedDeptIds == null) {
            return allDepts;
        }
        markChecked(allDepts, checkedDeptIds);
        return allDepts;
    }

    @Override
    public List<DeptVO> filterExcludeDept(List<DeptVO> depts, Long excludeDeptId) {
        return depts.stream()
                .filter(dept -> !dept.getId().equals(excludeDeptId))
                .map(dept -> {
                    if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
                        List<DeptVO> filtered = filterExcludeDept(dept.getChildren(), excludeDeptId);
                        DeptVO copy = copyVO(dept);
                        copy.setChildren(filtered);
                        return copy;
                    }
                    return dept;
                })
                .collect(Collectors.toList());
    }

    private void markChecked(List<DeptVO> depts, List<Long> checkedIds) {
        for (DeptVO dept : depts) {
            if (checkedIds.contains(dept.getId())) {
                dept.setChecked(true);
            }
            if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
                markChecked(dept.getChildren(), checkedIds);
            }
        }
    }

    private SysDept convertToSysDept(DeptDTO dto) {
        SysDept dept = new SysDept();
        dept.setDeptId(dto.getId());
        dept.setParentId(dto.getParentId());
        dept.setOrgCode(dto.getDeptCode());
        dept.setDeptName(dto.getDeptName());
        dept.setOrderNum(dto.getSort());
        dept.setSort(dto.getSort());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setEmail(dto.getEmail());
        dept.setStatus(dto.getStatus());
        return dept;
    }

    private DeptVO convertToVO(SysDept dept) {
        DeptVO vo = DeptVO.builder()
                .id(dept.getDeptId())
                .parentId(dept.getParentId())
                .deptName(dept.getDeptName())
                .deptCode(dept.getOrgCode())
                .sort(dept.getSort() != null ? dept.getSort() : dept.getOrderNum())
                .leader(dept.getLeader())
                .phone(dept.getPhone())
                .email(dept.getEmail())
                .status(dept.getStatus())
                .build();
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            vo.setChildren(dept.getChildren().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    private DeptVO copyVO(DeptVO dept) {
        return DeptVO.builder()
                .id(dept.getId())
                .parentId(dept.getParentId())
                .deptName(dept.getDeptName())
                .deptCode(dept.getDeptCode())
                .sort(dept.getSort())
                .leader(dept.getLeader())
                .phone(dept.getPhone())
                .email(dept.getEmail())
                .status(dept.getStatus())
                .build();
    }
}
