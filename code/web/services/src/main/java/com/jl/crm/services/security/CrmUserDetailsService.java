package com.jl.crm.services.security;

import com.jl.crm.services.CrmService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.*;

@Component ("userService")
public class CrmUserDetailsService implements UserDetailsService {

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
	public static class CrmUserDetails extends com.jl.crm.services.User implements UserDetails {
		public static final String SCOPE_READ = "read";
		public static final String SCOPE_WRITE = "write";
		public static final String ROLE_USER = "ROLE_USER";
		private Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

		public CrmUserDetails(com.jl.crm.services.User user) {
			super(user);
			Assert.notNull(user, "the provided user reference can't be null");
			for (String ga : Arrays.asList(ROLE_USER, SCOPE_READ, SCOPE_WRITE)) {
				this.grantedAuthorities.add(new SimpleGrantedAuthority(ga));
			}
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