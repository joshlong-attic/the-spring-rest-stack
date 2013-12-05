package com.jl.crm.services.security;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

@Component("userService")
public class CrmUserDetailsService implements UserDetailsService {



	private static final long serialVersionUID = 8188998571705670332L;

    private CrmService crmService;

    @Inject
    public CrmUserDetailsService(CrmService crmService) {
        this.crmService = crmService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.jl.crm.services.User user = crmService.findUserByUsername(username);
        return new CrmUserDetails(user);
    }

    @SuppressWarnings("serial")
    public static class CrmUserDetails extends User implements UserDetails {

        public static final String SCOPE_READ = "read";
        public static final String SCOPE_WRITE = "write";
        public static final String ROLE_USER = "ROLE_USER";
        private Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        public CrmUserDetails(com.jl.crm.services.User user) {
            super(user);
            Assert.notNull(user, "the provided user reference can't be null");
            this.grantedAuthorities = AuthorityUtils.createAuthorityList(ROLE_USER, SCOPE_READ, SCOPE_WRITE);
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

}
