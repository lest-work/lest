package com.lest.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.Assert;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.domain.SysRole;
import com.lest.modules.auth.entity.domain.SysRoleMenu;
import com.lest.modules.auth.entity.domain.SysUserRole;
import com.lest.modules.auth.entity.dto.RoleDTO;
import com.lest.modules.auth.entity.vo.RoleVO;
import com.lest.modules.auth.mapper.SysRoleMapper;
import com.lest.modules.auth.mapper.SysRoleMenuMapper;
import com.lest.modules.auth.mapper.SysUserRoleMapper;
import com.lest.modules.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public PageResult<RoleVO> page(Integer page, Integer size, String roleCode, String roleName, Integer status) {
        Page<SysRole> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (roleCode != null && !roleCode.isEmpty()) wrapper.like(SysRole::getRoleCode, roleCode);
        if (roleName != null && !roleName.isEmpty()) wrapper.like(SysRole::getRoleName, roleName);
        if (status != null) wrapper.eq(SysRole::getStatus, status);
        wrapper.orderByAsc(SysRole::getSort).orderByDesc(SysRole::getCreatedAt);
        Page<SysRole> result = roleMapper.selectPage(pageParam, wrapper);
        List<RoleVO> records = result.getRecords().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public RoleVO getById(Long id) {
        SysRole role = roleMapper.selectById(id);
        Assert.notNull(role, ErrorCode.ROLE_NOT_FOUND);
        return convertToVO(role);
    }

    @Override
    public List<RoleVO> listAll() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRole::getSort).orderByDesc(SysRole::getCreatedAt);
        return roleMapper.selectList(wrapper).stream()
                .map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Long create(RoleDTO dto) {
        Assert.isNull(roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, dto.getRoleCode())
                        .eq(SysRole::getDeleted, 0)),
                ErrorCode.ROLE_CODE_EXISTS);

        SysRole role = new SysRole();
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);

        roleMapper.insert(role);

        if (dto.getMenuIds() != null && dto.getMenuIds().length > 0) {
            assignMenus(role.getId(), dto.getMenuIds());
        }

        log.info("创建角色成功: roleId={}, roleCode={}", role.getId(), dto.getRoleCode());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.ROLE_NOT_FOUND);
        SysRole role = roleMapper.selectById(dto.getId());
        Assert.notNull(role, ErrorCode.ROLE_NOT_FOUND);

        if (dto.getRoleCode() != null && !dto.getRoleCode().equals(role.getRoleCode())) {
            Assert.isNull(roleMapper.selectOne(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getRoleCode, dto.getRoleCode())
                            .eq(SysRole::getDeleted, 0)
                            .ne(SysRole::getId, dto.getId())),
                    ErrorCode.ROLE_CODE_EXISTS);
        }

        SysRole updateRole = new SysRole();
        updateRole.setId(dto.getId());
        if (dto.getRoleCode() != null) updateRole.setRoleCode(dto.getRoleCode());
        if (dto.getRoleName() != null) updateRole.setRoleName(dto.getRoleName());
        if (dto.getDescription() != null) updateRole.setDescription(dto.getDescription());
        if (dto.getStatus() != null) updateRole.setStatus(dto.getStatus());
        if (dto.getSort() != null) updateRole.setSort(dto.getSort());

        roleMapper.updateById(updateRole);

        if (dto.getMenuIds() != null) {
            assignMenus(dto.getId(), dto.getMenuIds());
        }

        log.info("更新角色成功: roleId={}", dto.getId());
    }

    /**
     * 删除角色（单个或批量）
     * - ids不为空时批量删除
     * - ids为空时删除指定id的记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            // 批量删除，跳过超级管理员检查（前端负责过滤）
            for (Long roleId : ids) {
                doDelete(roleId);
            }
        } else {
            doDelete(id);
        }
    }

    private void doDelete(Long id) {
        Assert.isFalse(id.equals(SUPER_ADMIN_ROLE_ID), ErrorCode.ROLE_DELETE_SUPER_ADMIN);
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(id);
        if (!userIds.isEmpty()) {
            throw new com.lest.common.core.BusinessException(
                    ErrorCode.ROLE_HAS_USERS.getCode(),
                    ErrorCode.ROLE_HAS_USERS.getMessage());
        }
        roleMapper.deleteById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        log.info("删除角色成功: roleId={}", id);
    }

    @Override
    public List<Long> getMenuIds(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, Long[] menuIds) {
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null && menuIds.length > 0) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
        log.info("分配角色权限成功: roleId={}, menuIds={}", roleId, Arrays.toString(menuIds));
    }

    /** 实体转VO */
    private RoleVO convertToVO(SysRole role) {
        return RoleVO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .status(role.getStatus())
                .sort(role.getSort())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
