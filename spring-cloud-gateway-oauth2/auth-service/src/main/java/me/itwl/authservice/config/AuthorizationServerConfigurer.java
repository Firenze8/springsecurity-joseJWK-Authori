package me.itwl.authservice.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@EnableAuthorizationServer
@Configuration
@AllArgsConstructor
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManagerBean;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("test-client")
                .secret(passwordEncoder.encode("test-secret"))
                .authorizedGrantTypes("refresh_token", "password")
                .scopes("default-scope");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManagerBean)
                .accessTokenConverter(accessTokenConverter()); //AccessToken转换器-定义token的生成方式
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .allowFormAuthenticationForClients(); //如果没有这一句，他只接受post请求
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    //keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore demojwt.jks -storepass mypass;
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("demojwt.jks"), "mypass".toCharArray());
        //return keyStoreKeyFactory.getKeyPair("jwt", "mytest".toCharArray());
        return keyStoreKeyFactory.getKeyPair("mytest");
    }
}
