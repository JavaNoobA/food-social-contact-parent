package com.imooc.oauth2.config;

import com.imooc.commons.model.domain.SignInIdentity;
import com.imooc.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author pengfei.zhao
 * @date 2020/12/24 19:45
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private ClientOAuth2DataConfiguration clientConfiguration;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisTokenStore redisTokenStore;

    @Resource
    private UserService userService;

    // 配置令牌端点约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许访问 token 的公钥, 默认 /oauth/token_key 受保护的
        security.tokenKeyAccess("permitAll()")
                // 允许检查 token 的状态, 默认 /oauth/check_token 受保护的
                .checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientConfiguration.getClientId())
                .secret(passwordEncoder.encode(clientConfiguration.getSecret()))
                .authorizedGrantTypes(clientConfiguration.getGrantTypes())
                .accessTokenValiditySeconds(clientConfiguration.getTokenValidityTime())
                .refreshTokenValiditySeconds(clientConfiguration.getRefreshTokenValidityTime())
                .scopes(clientConfiguration.getScopes());
    }

    // 配置授权以及令牌的访问端点和令牌服务
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userService)
                .tokenStore(redisTokenStore)
                .tokenEnhancer((accessToken, authentication) -> {
                    SignInIdentity signInIdentity = (SignInIdentity) authentication.getPrincipal();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("nickname", signInIdentity.getNickname());
                    map.put("avatarUrl", signInIdentity.getAvatarUrl());
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    token.setAdditionalInformation(map);
                    return token;
                });
    }
}
