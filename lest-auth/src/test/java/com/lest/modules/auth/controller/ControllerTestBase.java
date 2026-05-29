package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.lest.modules.auth.service.*;
import com.lest.modules.auth.security.*;
import com.lest.common.security.util.LoginUser;

@WebMvcTest(controllers = {
    AuthController.class,
    UserController.class,
    RoleController.class,
    MenuController.class,
    OrganizationController.class,
    DictionaryController.class,
    DictionaryDataController.class
})
@Import(TestSecurityConfig.class)
public abstract class ControllerTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected RoleService roleService;

    @MockitoBean
    protected MenuService menuService;

    @MockitoBean
    protected OrganizationService organizationService;

    @MockitoBean
    protected DictionaryService dictionaryService;

    protected LoginUser createMockLoginUser(Long userId, String username) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername(username);
        return loginUser;
    }
}
