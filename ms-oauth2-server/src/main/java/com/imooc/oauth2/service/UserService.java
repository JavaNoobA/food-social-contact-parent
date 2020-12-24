package com.imooc.oauth2.service;

import com.imooc.commons.model.domain.SignInIdentity;
import com.imooc.commons.model.pojo.Diners;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.oauth2.mapper.DinersMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author pengfei.zhao
 * @date 2020/12/24 19:56
 */
@Service
public class UserService implements UserDetailsService {

    @Resource
    private DinersMapper dinersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AssertUtil.isNotEmpty(username, "用户名不能为空");
        Diners diners = dinersMapper.selectByAccountInfo(username);
        if (diners == null){
            throw new UsernameNotFoundException("该用户不存在!");
        }
        SignInIdentity signInIdentity = new SignInIdentity();
        BeanUtils.copyProperties(diners, signInIdentity);
        return signInIdentity;
    }
}
