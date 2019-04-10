package com.coronation.collections.security;

import com.coronation.collections.domain.Role;
import com.coronation.collections.domain.Task;
import com.coronation.collections.domain.User;
import com.coronation.collections.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Toyin on 2/17/19.
 */

@Service
public class ProfileDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userDao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new ProfileDetails(user);
    }
}
