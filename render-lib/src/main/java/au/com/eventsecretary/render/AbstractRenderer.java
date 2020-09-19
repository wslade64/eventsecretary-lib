package au.com.eventsecretary.render;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Intrinsic;
import au.com.auspost.simm.model.Type;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.simm.DateUtility;
import au.com.eventsecretary.simm.ExtensionUtils;
import au.com.eventsecretary.simm.IdentifiableUtils;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class AbstractRenderer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static String alias(Attribute attribute) {
        return ExtensionUtils.alias(attribute);
    }

    public static Attribute attribute(Map<String, ComplexType> model, String classifer, String attributeName) {
        ComplexType complexType = model.get(classifer);
        return complexType.getAttributes().stream().filter(aAttribute -> aAttribute.getName().equals(attributeName)).findFirst().get();
    }

    public static String resolveValue(Map<String, ComplexType> model, Attribute attribute, String value) {
        if (attribute.getType() == Type.ENUM) {
            ComplexType enumComplexType = model.get(attribute.getClassifier());
            Optional<Attribute> first = enumComplexType.getAttributes().stream().filter(aAttribute -> aAttribute.getName().equals(value)).findFirst();
            if (!first.isPresent()) {
                return value;
            }
            return alias(first.get());
        }
        if (attribute.getType() == Type.INTRINSIC) {
            if (attribute.getClassifier().equals(Intrinsic.BOOLEAN.name())) {
                return "true".equals(value) ? "Yes" : "No";
            }
        }
        return value;
    }

    public static String resolveEnum(String value, String complexTypeName, Map<String, ComplexType> model) {
        ComplexType complexType = model.get(complexTypeName);
        Optional<Attribute> first = complexType.getAttributes().stream().filter(attribute -> attribute.getName().equals(value)).findFirst();
        if (!first.isPresent()) {
            return value;
        }
        return alias(first.get());
    }

    public static Map<String, Object> createModel()
    {
        Map<String, Object> model = new HashMap<>();

        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        try
        {
            model.put("DateUtility", staticModels.get(DateUtility.class.getName()));
            model.put("Util", staticModels.get(IdentifiableUtils.class.getName()));
            model.put("Tools", staticModels.get(AbstractRenderer.class.getName()));
        }
        catch (TemplateModelException e)
        {
            throw new UnexpectedSystemException(e);
        }

        return model;
    }

    public static void addUtility(Map<String, Object> model, String logicalName, String className) {
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        try
        {
            model.put(logicalName, staticModels.get(className));
        }
        catch (TemplateModelException e)
        {
            throw new UnexpectedSystemException(e);
        }
    }

}
