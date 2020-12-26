package com.imooc.gateway.filter;

import com.imooc.gateway.component.HandleException;
import com.imooc.gateway.config.IgnoreUrlsConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author pengfei.zhao
 * @date 2020/12/26 8:48
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Resource
    private HandleException handleException;
    @Resource
    private RestTemplate restTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean flag = false;
        String currentPath = exchange.getRequest().getURI().getPath();
        AntPathMatcher matcher = new AntPathMatcher();
        for (String url : ignoreUrlsConfig.getUrls()) {
            if (matcher.match(url, currentPath)){
                flag = true;
                break;
            }
        }
        // 白名单中放行
        if (flag) {
            return chain.filter(exchange);
        }

        String accessToken = exchange.getRequest().getQueryParams().getFirst("accessToken");
        if (StringUtils.isBlank(accessToken)){
            return handleException.writeError(exchange, "请登录");
        }

        String checkTokenUrl = "http://ms-oauth2-server/oauth/check_token?token=".concat(accessToken);
        try {
            // 发送远程请求，验证 token
            ResponseEntity<String> entity = restTemplate.getForEntity(checkTokenUrl, String.class);
            // token 无效的业务逻辑处理
            if (entity.getStatusCode() != HttpStatus.OK) {
                return handleException.writeError(exchange,
                        "Token was not recognised, token: ".concat(accessToken));
            }
            if (StringUtils.isBlank(entity.getBody())) {
                return handleException.writeError(exchange,
                        "This token is invalid: ".concat(accessToken));
            }
        } catch (Exception e) {
            return handleException.writeError(exchange,
                    "Token was not recognised, token: ".concat(accessToken));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
