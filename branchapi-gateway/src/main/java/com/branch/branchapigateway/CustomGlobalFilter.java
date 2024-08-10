package com.branch.branchapigateway;

import com.branch.branchapiclientsdk.model.User;
import com.branch.branchapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
@Configuration
public class CustomGlobalFilter implements GlobalFilter, Ordered {

//    @DubboReference
//    private InnerUserService innerUserService;
//
//    @DubboReference
//    private InnerInterfaceInfoService innerInterfaceInfoService;
//
//    @DubboReference
//    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";
    /**
     * 全局过滤
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        1.用户发送请求到 API 网关
//        2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + request.getPath().value());
        log.info("请求方法：" + request.getMethod());
        log.info("请求参数：" + request.getQueryParams());
        log.info("请求头：" + request.getHeaders());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        //3.黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            log.info("黑名单：" + sourceAddress + "访问失败");
            //设置一个状态码直接拦截掉
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

//        4.用户鉴权(判断 ak、sk 是否台法)
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        //todo 实际情况：从数据库中查询
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
//        if (!"yupi".equals(accessKey)){
//            return handleNoAuth(response);
//        }
        if (Long.parseLong(nonce) > 10000L){
            return handleNoAuth(response);
        }
        //时间和当前时间不能超过五分钟
        //假定 timestamp 是一个 long 类型的值，代表秒数
        long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if (currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
            return handleNoAuth(response);
        }
//        long timestamplong;
//        try {
//            timestamplong = Long.parseLong(timestamp);
//            if (timestamplong - (System.currentTimeMillis() / 1000) > 60 * 5) {
//                throw new RuntimeException("timestamp 不正确");
//            }
//        } catch (NumberFormatException e) {
//            // 处理 timestampStr 无法解析为 long 的情况
//            System.err.println("无效的时间戳格式");
//        }

        //实际情况：从数据库中查询
        String serverSign = SignUtils.getSign(body, "abcdefgh");
        if (!sign.equals(serverSign)){
            return handleNoAuth(response);
        }
        //todo 5.请求的模拟接口是否存在?
        //判断:从数据库中查询模拟接口url是否存在，以及请求方法是否匹配
//        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("url", request.getPath().value());
//        queryWrapper.eq("method", request.getMethod().toString());
//        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
//        if (interfaceInfo == null) {
//            return handleNoAuth(response);
//        }




//        6.请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
//        7.响应日志
//        return handleResponse(exchange, chain);
//
//
////       todo 8.调用成功，接口调用次数 +1  invokeCount
//        if (response.getStatusCode() == HttpStatus.OK){
//
//        }  else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
//            return handleInvokeError(response);
//        }

//        9.调用失败，返回一个规范的错误码
//        if (response.getStatusCode() != HttpStatus.OK) {
//            return handleInvokeError(response);
//        }
//
//        log.info("custom global filter");
//        return chain.filter(exchange);
//    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }





    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }


    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }


}


