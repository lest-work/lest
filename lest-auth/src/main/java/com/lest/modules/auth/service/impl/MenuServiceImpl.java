package com.lest.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.Assert;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.modules.auth.entity.domain.SysMenu;
import com.lest.modules.auth.entity.domain.SysRole;
import com.lest.modules.auth.entity.domain.SysUserRole;
import com.lest.modules.auth.entity.dto.MenuDTO;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.entity.vo.RouteVO;
import com.lest.modules.auth.mapper.SysMenuMapper;
import com.lest.modules.auth.mapper.SysRoleMapper;
import com.lest.modules.auth.mapper.SysRoleMenuMapper;
import com.lest.modules.auth.mapper.SysUserRoleMapper;
import com.lest.modules.auth.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final Long ROOT_MENU_ID = 0L;
    private static final String SUPER_PERMISSION = "*:*:*";

    private final SysMenuMapper menuMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public PageResult<MenuVO> page(Integer page, Integer size, String menuName, String path, String permission) {
        Page<SysMenu> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (menuName != null && !menuName.isEmpty()) wrapper.like(SysMenu::getMenuName, menuName);
        if (path != null && !path.isEmpty()) wrapper.like(SysMenu::getPath, path);
        if (permission != null && !permission.isEmpty()) wrapper.like(SysMenu::getPermission, permission);
        wrapper.orderByAsc(SysMenu::getSort).orderByDesc(SysMenu::getCreatedAt);
        Page<SysMenu> result = menuMapper.selectPage(pageParam, wrapper);
        List<MenuVO> records = result.getRecords().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public List<MenuVO> getTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort).orderByDesc(SysMenu::getCreatedAt);
        List<SysMenu> allMenus = menuMapper.selectList(wrapper);
        return buildTree(allMenus, ROOT_MENU_ID);
    }

    @Override
    public List<RouteVO> getRoutes(Long userId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuType, 1).or().eq(SysMenu::getMenuType, 2);
        wrapper.eq(SysMenu::getStatus, 1);

        if (!isSuperAdmin(userId)) {
            List<Long> menuIds = roleMenuMapper.selectMenuIdsByUserId(userId);
            if (menuIds == null || menuIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(SysMenu::getId, menuIds);
        }

        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(wrapper);

        return menus.stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId().equals(ROOT_MENU_ID))
                .map(this::convertToRoute)
                .collect(Collectors.toList());
    }

    @Override
    public MenuVO getById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        Assert.notNull(menu, ErrorCode.MENU_NOT_FOUND);
        return convertToVO(menu);
    }

    @Override
    public List<MenuVO> listAll() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort).orderByDesc(SysMenu::getCreatedAt);
        return menuMapper.selectList(wrapper).stream()
                .map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Long create(MenuDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : ROOT_MENU_ID);
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPermission(dto.getPermission());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : 0);
        menu.setAlwaysShow(dto.getAlwaysShow() != null ? dto.getAlwaysShow() : 0);
        menu.setRedirect(dto.getRedirect());
        menu.setMeta(dto.getMeta());

        menuMapper.insert(menu);

        log.info("创建菜单成功: menuId={}, menuName={}", menu.getId(), dto.getMenuName());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO dto) {
        Assert.notNull(dto.getId(), ErrorCode.MENU_NOT_FOUND);
        SysMenu menu = menuMapper.selectById(dto.getId());
        Assert.notNull(menu, ErrorCode.MENU_NOT_FOUND);

        SysMenu updateMenu = new SysMenu();
        updateMenu.setId(dto.getId());
        if (dto.getParentId() != null) updateMenu.setParentId(dto.getParentId());
        if (dto.getMenuName() != null) updateMenu.setMenuName(dto.getMenuName());
        if (dto.getMenuType() != null) updateMenu.setMenuType(dto.getMenuType());
        if (dto.getPath() != null) updateMenu.setPath(dto.getPath());
        if (dto.getComponent() != null) updateMenu.setComponent(dto.getComponent());
        if (dto.getPermission() != null) updateMenu.setPermission(dto.getPermission());
        if (dto.getIcon() != null) updateMenu.setIcon(dto.getIcon());
        if (dto.getSort() != null) updateMenu.setSort(dto.getSort());
        if (dto.getVisible() != null) updateMenu.setVisible(dto.getVisible());
        if (dto.getStatus() != null) updateMenu.setStatus(dto.getStatus());
        if (dto.getKeepAlive() != null) updateMenu.setKeepAlive(dto.getKeepAlive());
        if (dto.getAlwaysShow() != null) updateMenu.setAlwaysShow(dto.getAlwaysShow());
        if (dto.getRedirect() != null) updateMenu.setRedirect(dto.getRedirect());
        if (dto.getMeta() != null) updateMenu.setMeta(dto.getMeta());

        menuMapper.updateById(updateMenu);

        log.info("更新菜单成功: menuId={}", dto.getId());
    }

    /**
     * 删除菜单（单个或批量）
     * - ids不为空时批量删除
     * - ids为空时删除指定id的记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long menuId : ids) {
                doDelete(menuId);
            }
        } else {
            doDelete(id);
        }
    }

    private void doDelete(Long id) {
        Assert.isFalse(id.equals(ROOT_MENU_ID), ErrorCode.MENU_DELETE_ROOT);
        List<SysMenu> children = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        Assert.isTrue(children.isEmpty(), ErrorCode.MENU_HAS_CHILDREN);
        menuMapper.deleteById(id);
        log.info("删除菜单成功: menuId={}", id);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        if (isSuperAdmin(userId)) {
            return List.of(SUPER_PERMISSION);
        }
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByUserId(userId);
        if (menuIds == null || menuIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getId, menuIds);
        wrapper.isNotNull(SysMenu::getPermission);
        wrapper.ne(SysMenu::getPermission, "");
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return menus.stream()
                .map(SysMenu::getPermission)
                .filter(Objects::nonNull)
                .filter(p -> !p.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
        return roles.stream().anyMatch(r -> r != null && Objects.equals(r.getIsSuper(), 1));
    }

    /** 构建菜单树 */
    private List<MenuVO> buildTree(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysMenu::getSort))
                .map(menu -> {
                    MenuVO vo = convertToVO(menu);
                    vo.setChildren(buildTree(allMenus, menu.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /** 实体转VO */
    private MenuVO convertToVO(SysMenu menu) {
        return MenuVO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .menuName(menu.getMenuName())
                .menuType(menu.getMenuType())
                .path(menu.getPath())
                .component(menu.getComponent())
                .permission(menu.getPermission())
                .icon(menu.getIcon())
                .sort(menu.getSort())
                .visible(menu.getVisible())
                .status(menu.getStatus())
                .keepAlive(menu.getKeepAlive())
                .alwaysShow(menu.getAlwaysShow())
                .redirect(menu.getRedirect())
                .meta(menu.getMeta())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    /** 转换为路由VO */
    private RouteVO convertToRoute(SysMenu menu) {
        RouteVO.RouteVOBuilder builder = RouteVO.builder()
                .name(menu.getPath() != null ? capitalizeFirst(menu.getPath().replace("/", "")) : null)
                .path(menu.getPath())
                .component(menu.getComponent())
                .redirect(menu.getRedirect())
                .icon(menu.getIcon())
                .hidden(menu.getVisible() != null && menu.getVisible() == 0);

        if (menu.getMenuType() == 1 || menu.getMenuType() == 2) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getParentId, menu.getId());
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSort);
            List<SysMenu> children = menuMapper.selectList(wrapper);

            List<RouteVO> childRoutes = children.stream()
                    .filter(child -> child.getMenuType() == 1 || child.getMenuType() == 2)
                    .map(this::convertToRoute)
                    .collect(Collectors.toList());

            builder.children(childRoutes.isEmpty() ? null : childRoutes);
        }

        builder.meta(RouteVO.MetaVO.builder()
                .title(menu.getMenuName())
                .icon(menu.getIcon())
                .keepAlive(menu.getKeepAlive() != null && menu.getKeepAlive() == 1)
                .alwaysShow(menu.getAlwaysShow() != null && menu.getAlwaysShow() == 1)
                .build());

        return builder.build();
    }

    /** 首字母大写 */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
