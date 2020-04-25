package au.com.eventsecretary.render;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RenderConfiguration {
    @Bean
    public Renderer renderer(freemarker.template.Configuration freemarkerConfiguration) {
        return new FreemarkerRenderer(freemarkerConfiguration);
    }
}
