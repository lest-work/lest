package com.lest.modules.system.service.impl;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.modules.system.entity.dto.MenuDTO;
import com.lest.modules.system.entity.vo.MenuVO;
import com.lest.modules.system.service.MenuService;
import com.lest.system.api.RemoteMenuService;
import com.lest.system.api.RemoteUserService;
import com.lest.system.api.domain.vo.SysMenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现（通过 Feign 调用 lest-auth）
 *
 * @author yshan2028
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final String SOURCE = "system";

    private final RemoteMenuService remoteMenuService;
    private final RemoteUserService remoteUserService;

    @Override
    public List<MenuVO> selectMenuList(String menuName, String path, String permission) {
        R<List<SysMenuVO>> result = remoteMenuService.listAll(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .filter(menu -> menuName == null || menuName.isEmpty() ||
                        menu.getMenuName() != null && menu.getMenuName().contains(menuName))
                .filter(menu -> path == null || path.isEmpty() ||
                        menu.getPath() != null && menu.getPath().contains(path))
                .filter(menu -> permission == null || permission.isEmpty() ||
                        menu.getPermission() != null && menu.getPermission().contains(permission))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuVO> selectMenuTree() {
        R<List<SysMenuVO>> result = remoteMenuService.getTree(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public MenuVO getById(Long menuId) {
        R<SysMenuVO> result = remoteMenuService.getById(menuId, SOURCE);
        if (result.getData() == null) {
            return null;
        }
        return convertToVO(result.getData());
    }

    @Override
    public Long createMenu(MenuDTO dto) {
        return 0L;
    }

    @Override
    public void updateMenu(MenuDTO dto) {
    }

    @Override
    public void deleteMenu(Long menuId) {
    }

    @Override
    public List<MenuVO> buildRoleMenuTree(List<MenuVO> allMenus, List<Long> checkedMenuIds) {
        if (checkedMenuIds == null) {
            return allMenus;
        }
        markChecked(allMenus, checkedMenuIds);
        return allMenus;
    }

    @Override
    public List<Long> getMenuIdsByUserId(Long userId) {
        R<List<Long>> result = remoteUserService.getRoleIds(userId, SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData();
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        // TODO: implement via Feign call to lest-auth
        return List.of();
    }

    private void markChecked(List<MenuVO> menus, List<Long> checkedIds) {
        for (MenuVO menu : menus) {
            if (checkedIds.contains(menu.getId())) {
                menu.setChecked(true);
            }
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                markChecked(menu.getChildren(), checkedIds);
            }
        }
    }

    private MenuVO convertToVO(SysMenuVO menu) {
        MenuVO vo = MenuVO.builder()
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
                .checked(menu.getChecked())
                .build();
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            vo.setChildren(menu.getChildren().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
        }
        return vo;
    }
}
