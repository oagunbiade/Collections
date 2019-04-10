package com.coronation.collections.security;

import com.coronation.collections.domain.Task;
import com.coronation.collections.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Created by Toyin on 3/19/19.
 */
public class ProfileDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private User user;
    private List<GrantedAuthority> authorities;

    public ProfileDetails(User user) {
        this.user = user;
        authorities = new ArrayList<>();
        addAuthorities();
    }

    private void addAuthorities() {
        for (Task task: user.getRole().getTasks()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + task.getName()));
        }
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    };

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    public String getPassword() {
        return user.getPassword();
    };

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>
     * .
     *
     * @return the username (never <code>null</code>)
     */
    public String getUsername() {
        return user.getUserName();
    };

    public String getFirstName() {
        return user.getUserProfile().getFirstName();
    }

    public String getLastName() {
        return user.getUserProfile().getLastName();
    }
    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isAccountNonExpired() {
        return true;
    };

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    public boolean isAccountNonLocked() {
        return true;
    };

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    public boolean isCredentialsNonExpired() {
        return true;
    };

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return true;
    };

    public User toUser() {
        return user;
    }
}