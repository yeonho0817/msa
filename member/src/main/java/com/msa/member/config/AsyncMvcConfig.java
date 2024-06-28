package com.msa.member.config;

import com.msa.member.interceptor.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncMvcConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(30);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Task-Executor-");
        taskExecutor.initialize();
//        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
        return taskExecutor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(taskExecutor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin","Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .maxAge(10);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger*/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(List.of(
                        "/v3/api-docs*/**",
                        "/swagger-ui/**"
                ));
    }

}


