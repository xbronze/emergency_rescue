package com.china.rescue.controller;

import com.china.rescue.common.ServerResponse;
import com.china.rescue.po.User;
import com.china.rescue.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@Api(value = "用户管理 controller" ,tags = {"用户管理接口统计"})
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @GetMapping(value = "/{id}")
    public ServerResponse<User> loginWithId(@PathVariable("id") Long id){
        return ServerResponse.createBySuccessByData(userService.findUserById(id));
    }

    @ApiOperation(value = "获取用户信息接口")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/info")
    public String info(){
        String userDetails = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails){
            userDetails = ((UserDetails) principal).getUsername();
        } else {
            userDetails = principal.toString();
        }
        return userDetails;
    }
}
