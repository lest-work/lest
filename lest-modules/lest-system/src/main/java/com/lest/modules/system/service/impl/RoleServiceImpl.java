package com.lest.modules.system.service.impl;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.modules.system.entity.dto.RoleDTO;
import com.lest.modules.system.entity.vo.RoleVO;
import com.lest.modules.system.service.RoleService;
import com.lest.system.api.RemoteRoleService;
import com.lest.system.api.RemoteUserService;
import com.lest.system.api.domain.SysRole;
import com.lest.system.api.domain.SysUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现（通过 Feign 调用 lest-auth）
 *
 * @author yshan2028
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String SOURCE = "system";

    private final RemoteRoleService remoteRoleService;
    private final RemoteUserService remoteUserService;

    @Override
    public List<RoleVO> selectRoleList(String roleCode, String roleName, Integer status) {
        R<List<SysRole>> result = remoteRoleService.listAll(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .filter(role -> roleCode == null || roleCode.isEmpty() ||
                        role.getRoleCode() != null && role.getRoleCode().contains(roleCode))
                .filter(role -> roleName == null || roleName.isEmpty() ||
                        role.getRoleName() != null && role.getRoleName().contains(roleName))
                .filter(role -> status == null ||
                        role.getStatus() != null && role.getStatus().equals(status))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleVO> selectAllRoles() {
        R<List<SysRole>> result = remoteRoleService.listAll(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleVO getById(Long roleId) {
        R<SysRole> result = remoteRoleService.getById(roleId, SOURCE);
        if (result.getData() == null) {
            return null;
        }
        return convertToVO(result.getData());
    }

    @Override
    public Long createRole(RoleDTO dto) {
        return 0L;
    }

    @Override
    public void updateRole(RoleDTO dto) {
    }

    @Override
    public void deleteRole(Long roleId) {
    }

    @Override
    public void updateRoleStatus(Long roleId, Integer status) {
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        R<List<Long>> result = remoteRoleService.getMenuIds(roleId, SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData();
    }

    @Override
    public void updateAuthRole(Long roleId, Long[] menuIds) {
        remoteRoleService.updateMenus(roleId, menuIds, SOURCE);
    }

    @Override
    public void exportRole(List<RoleVO> list) {
    }

    @Override
    public void updateDataScope(RoleDTO dto) {
    }

    private RoleVO convertToVO(SysRole role) {
        return RoleVO.builder()
                .id(role.getRoleId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .status(role.getStatus())
                .isSuper(role.getIsSuper())
                .sort(role.getSort())
                .build();
    }
}
