package me.itwl.gatewayservice.authorization;

import cn.hutool.core.collection.CollUtil;
import me.itwl.gatewayservice.constant.AuthConstant;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    List<String> authorities = null;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        URI uri = authorizationContext.getExchange().getRequest().getURI();
        Map<String, List<String>> resourceRolesMap = new LinkedHashMap<>();
        resourceRolesMap.put("/api/hello", CollUtil.toList("ROLE_USER"));
        resourceRolesMap.put("/api/user/hello", CollUtil.toList("ROLE_ADMIN"));
        resourceRolesMap.put("/api/user/**", CollUtil.toList("ROLE_USER"));
        //pattern start
        Set<String> urlPatterns = resourceRolesMap.keySet();
        for(String pattern : urlPatterns){
            boolean match = false;
            if(antPathMatcher.isPattern(pattern)){
                match = antPathMatcher.match(pattern, uri.getPath());
                System.out.println(uri.getPath());
            } else{
                match = uri.getPath().equals(pattern);
            }
            if(match){
                authorities = resourceRolesMap.get(pattern);
                break;
            }
        }
        //pattern ends
        //authorities = resourceRolesMap.get(uri.getPath());
        authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(c->{
                    //检测权限是否匹配
                    String[] roles = c.split(",");
                    for(String role:roles) {
                        if(authorities.contains(role)) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
