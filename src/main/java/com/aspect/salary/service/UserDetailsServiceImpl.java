package com.aspect.salary.service;

import com.aspect.salary.dao.AppUserDAO;
import com.aspect.salary.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserDAO appUserDAO;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException{
        AppUser appUser = this.appUserDAO.findUserAccount(login);

        if(appUser == null){
            throw new UsernameNotFoundException("Login " + login + " wasn't found in the database");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("superuser");
        List<GrantedAuthority> roleNames = new ArrayList<>();
        roleNames.add(authority);

        return (UserDetails) new User(appUser.getLogin(),appUser.getPassword(),roleNames);
    }
}
