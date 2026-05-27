package com.lest.modules.auth.service;

import com.lest.common.base.PageResult;
import com.lest.modules.auth.entity.dto.UserDTO;
import com.lest.modules.auth.entity.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author yshan2028
 * @since 2026-05-26
 */
public interface UserService {

    PageResult<UserVO> page(Integer page, Integer size, String username, String phone, Integer status, Long orgId);
    UserVO getById(Long id);
    List<UserVO> listAll();
    Long create(UserDTO dto);
    void update(UserDTO dto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void resetPassword(Long id);
    List<Long> getRoleIds(Long userId);
    void assignRoles(Long userId, Long[] roleIds);
}
