package ${packageName}.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SampleUserDetails implements UserDetails {

    private static final long serialVersionUID = -8550494461053843289L;
    private final Map<String, String> siteMinderAttributes;
    private final String userName;

    public SampleUserDetails(String userName, Map<String, String> siteMinderAttributes) {
 this.userName = userName;
 this.siteMinderAttributes = siteMinderAttributes;
 }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return "pass";
    }

    public Map<String, String> getSiteMinderAttributes() {
        return siteMinderAttributes;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override

    public boolean isEnabled() {
        return true;

    }

}
