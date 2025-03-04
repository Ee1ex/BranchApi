package com.branch.branchapigateway.gateway;

import com.branch.branchapigateway.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableDubbo
@SpringBootApplication
public class BranchapiGatewayApplication {

    @DubboReference
    private DemoService demoService;
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(BranchapiGatewayApplication.class, args);
        BranchapiGatewayApplication application = context.getBean(BranchapiGatewayApplication.class);
        String result = application.doSayHello("world");
        String result2 = application.doSayHello2("world");
        System.out.println("result: " + result);
        System.out.println("result: " + result2);
    }

    public String doSayHello(String name) {
        return demoService.sayHello(name);
    }

    public String doSayHello2(String name) {
        return demoService.sayHello2(name);
    }

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("tobaidu", r -> r.path("/baidu")
//                        .uri("https://www.baidu.com"))
//                .route("toyupiicu", r -> r.path("/yupiicu")
//                        .uri("http://yupi.icu"))
//                .build();
//    }

}

