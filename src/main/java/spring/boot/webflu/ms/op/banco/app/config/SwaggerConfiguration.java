package spring.boot.webflu.ms.op.banco.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;
 	@Configuration
	@EnableSwagger2WebFlux
	public class SwaggerConfiguration {
	    @Bean
	    public Docket createRestApi() {
	    	return new Docket(DocumentationType.SWAGGER_2)
					.select()
					.apis(RequestHandlerSelectors.basePackage("spring.boot.webflu.ms.op.banco.app.controllers"))
					.paths(PathSelectors.any())
					.build();
	    }
	    
	    //http://localhost:8023/swagger-ui.html
}
