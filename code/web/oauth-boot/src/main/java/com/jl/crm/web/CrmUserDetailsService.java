package com.jl.crm.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.User;

/**
 * @author Josh Long
 */
public class CrmUserDetailsService implements UserDetailsService {

    CrmService crmService;

    CrmUserDetailsService(CrmService crmService) {
        this.crmService = crmService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.jl.crm.services.User user = crmService.findUserByUsername(username);
        return new CrmUserDetails(user);
    }
}

@SuppressWarnings("serial")
class CrmUserDetails extends User implements UserDetails {

    public static final String SCOPE_READ = "read";
    public static final String SCOPE_WRITE = "write";
    public static final String ROLE_USER = "ROLE_USER";

    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

    CrmUserDetails(com.jl.crm.services.User user) {
        super(user);
        this.grantedAuthorities = AuthorityUtils.createAuthorityList(
                ROLE_USER, SCOPE_READ, SCOPE_WRITE);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }
}