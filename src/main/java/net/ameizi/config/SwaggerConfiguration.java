package net.ameizi.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {

    @Value("${swagger.host}")
    private String swaggerHost;
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("amz")
                .description("基于shiro和jwt的前后端分离权限系统")
                .termsOfServiceUrl("")
                .version("1.0.0")
                .contact(new Contact("", "", ""))
                .build();
    }

    @Bean
    public Docket createRestApi() {
        ParameterBuilder builder = new ParameterBuilder();
        Parameter parameter = builder
                // 从cookie中获取token
                .parameterType("cookie") //参数类型支持header, cookie, body, query etc
                .name("token") //参数名
                .defaultValue("") //默认值
                .description("请输入token")
                .modelRef(new ModelRef("string")) //指定参数值的类型
                .required(false).build(); //非必需，这里是全局配置，然而在登陆的时候是不用验证的
        List<Parameter> parameters = Lists.newArrayList(parameter);
        return new Docket(DocumentationType.SWAGGER_2)
                .host(this.swaggerHost)
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.ameizi"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(this.apiInfo())
                .globalOperationParameters(parameters);
    }

    /**
     * swagger ui资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * swagger-ui.html路径映射，浏览器中使用/api-docs访问
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api-docs","/swagger-ui.html");
    }
}