package ${packageName}.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class SpringAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (!"pass".equals(auth.getCredentials()) && !(auth instanceof PreAuthenticatedAuthenticationToken)) {
            throw new BadCredentialsException("Auth Failed");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Map<String, String> headers = new LinkedHashMap<>();
        for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        SampleUserDetails sampleUserDetails = new SampleUserDetails(auth.getName(), headers);
        return new UsernamePasswordAuthenticationToken(sampleUserDetails, auth.getCredentials(), authorities);
    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.isAssignableFrom(UsernamePasswordAuthenticationToken.class)
            || auth.isAssignableFrom(PreAuthenticatedAuthenticationToken.class);
    }
}