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
	public void setCrmService(CrmService crmService) {
		this.crmService = crmService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.jl.crm.services.User user = crmService.findUserByUsername(username);
		return new CrmUserDetails(user);
	}

	@SuppressWarnings("serial")
	public static class CrmUserDetails implements UserDetails {
 		public static final String SCOPE_READ = "read";
		public static final String SCOPE_WRITE = "write";
 		public static final String ROLE_USER = "ROLE_USER";
		private Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		private com.jl.crm.services.User user;

		public CrmUserDetails(com.jl.crm.services.User user) {
			Assert.notNull(user, "the provided user reference can't be null");
			this.user = user;
			for (String ga : Arrays.asList(ROLE_USER, SCOPE_READ, SCOPE_WRITE)) {
				this.grantedAuthorities.add(new SimpleGrantedAuthority(ga));
			}
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.grantedAuthorities;
		}

		@Override
		public String getPassword() {
			return user.getPassword();
		}

		@Override
		public String getUsername() {
			return user.getUsername();
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

		@Override
		public boolean isEnabled() {
			return user.isEnabled();
		}

		public com.jl.crm.services.User getUser() {
			return this.user;
		}
	}
}
