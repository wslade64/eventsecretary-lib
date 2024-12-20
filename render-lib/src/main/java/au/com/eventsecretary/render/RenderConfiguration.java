package au.com.eventsecretary.render;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class RenderConfiguration {
    @Bean
    public Renderer renderer(freemarker.template.Configuration freemarkerConfiguration) {
        return new FreemarkerRenderer(freemarkerConfiguration);
    }
}
