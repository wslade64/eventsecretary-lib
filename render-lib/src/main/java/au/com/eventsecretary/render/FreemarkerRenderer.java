package au.com.eventsecretary.render;

import au.com.eventsecretary.UnexpectedSystemException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

/**
 * Use the Velocity engine to render text.
 *
 * @author Warwick Slade
 */
@Component
public class FreemarkerRenderer<T> implements Renderer<T>
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Configuration freemarkerConfiguration;

    public FreemarkerRenderer(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public String render(String contentIdentifier, T content)
    {
        try
        {
            Template template = freemarkerConfiguration.getTemplate(contentIdentifier);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, content);
        }
        catch (IOException e)
        {
            throw new UnexpectedSystemException(e);
        }
        catch (TemplateException e)
        {
            logger.error("render", e);
            throw new UnexpectedSystemException(e);
        }
    }
}
