package com.lest.modules.system.service.impl;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.modules.system.entity.dto.UserDTO;
import com.lest.modules.system.entity.vo.UserVO;
import com.lest.modules.system.service.UserService;
import com.lest.system.api.RemoteUserService;
import com.lest.system.api.domain.SysUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现（通过 Feign 调用 lest-auth）
 *
 * @author yshan2028
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String SOURCE = "system";

    private final RemoteUserService remoteUserService;

    @Override
    public List<UserVO> selectUserList(String username, String nickname, String phone, String email, Integer status, Long orgId) {
        R<List<SysUserVO>> result = remoteUserService.listAll(SOURCE);
        if (result.getData() == null) {
            return List.of();
        }
        return result.getData().stream()
                .filter(user -> username == null || username.isEmpty() ||
                        user.getUsername() != null && user.getUsername().contains(username))
                .filter(user -> nickname == null || nickname.isEmpty() ||
                        user.getNickname() != null && user.getNickname().contains(nickname))
                .filter(user -> phone == null || phone.isEmpty() ||
                        user.getPhone() != null && user.getPhone().contains(phone))
                .filter(user -> email == null || email.isEmpty() ||
                        user.getEmail() != null && user.getEmail().contains(email))
                .filter(user -> status == null ||
                        user.getStatus() != null && user.getStatus().equals(status))
                .filter(user -> orgId == null ||
                        user.getOrgId() != null && user.getOrgId().equals(orgId))
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserVO getById(Long userId) {
        R<SysUserVO> result = remoteUserService.getById(userId, SOURCE);
        if (result.getData() == null) {
            return null;
        }
        return convertToVO(result.getData());
    }

    @Override
    public Long createUser(UserDTO dto) {
        return 0L;
    }

    @Override
    public void updateUser(UserDTO dto) {
    }

    @Override
    public void deleteUser(Long userId) {
    }

    @Override
    public void resetPassword(Long userId, String password) {
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        // TODO: implement via Feign call to lest-auth
    }

    @Override
    public String[] getUserRoleIds(Long userId) {
        R<List<Long>> result = remoteUserService.getRoleIds(userId, SOURCE);
        if (result.getData() == null) {
            return new String[0];
        }
        return result.getData().stream()
                .map(String::valueOf)
                .toArray(String[]::new);
    }

    @Override
    public void cancelAuthRole(Long userId, Long roleId) {
    }

    @Override
    public void cancelAuthRoleAll(Long userId, Long[] roleIds) {
    }

    @Override
    public void selectAuthRole(Long userId, Long[] roleIds) {
    }

    @Override
    public void exportUser(List<UserVO> list) {
    }

    @Override
    public void importTemplate() {
    }

    @Override
    public String importUser(MultipartFile file, boolean updateSupport) throws Exception {
        return "";
    }

    private UserVO convertToVO(SysUserVO user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .sex(user.getSex())
                .status(user.getStatus())
                .orgId(user.getOrgId())
                .orgName(user.getOrgName())
                .roles(user.getRoles())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
