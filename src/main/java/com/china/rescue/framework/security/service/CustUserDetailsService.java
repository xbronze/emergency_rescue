package com.china.rescue.framework.security.service;

import com.china.rescue.business.system.mapper.UserMapper;
import com.china.rescue.business.system.po.LoginUser;
import com.china.rescue.business.system.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // 1. 查询用户
        User user = userMapper.selectUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User " + login + " was not found in db");
        }
        // 2. 设置角色
//        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(system.getRole());
//        grantedAuthorities.add(grantedAuthority);
//
//        return new org.springframework.security.core.userdetails.User(login, system.getPassword(), grantedAuthorities);

        Set<String> perms = new HashSet<>();
        perms.add(user.getRole());

        return new LoginUser(user, perms);
    }
}
