package au.com.eventsecretary.simm;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Documentation;
import au.com.auspost.simm.model.Extension;
import au.com.auspost.simm.model.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface ModelUtils {
    static String extensionValue(ModelExtensions modelExtensions, Attribute attribute, Map<String, String> extensions) {
        return extensionValue(modelExtensions, attribute, attribute.getId(), extensions);
    }

    static String extensionValue(ModelExtensions modelExtensions, Attribute attribute, String id, Map<String, String> extensions) {
        if (extensions == null) {
            return "";
        }
        String value = extensions.get(id);
        if (attribute.getType() == Type.ENUM && value != null) {
            ComplexType enumComplexType = modelExtensions.getComplexTypeExtensions().stream().filter(ct -> ct.getId().equals(attribute.getClassifier())).findFirst().get();
            for (Attribute enumAttribute : enumComplexType.getAttributes()) {
                if (enumAttribute.getId().equals(value)) {
                    value = ExtensionUtils.alias(enumAttribute);
                    break;
                }
            }
        }
        return value;
    }

    static Documentation findDocumentation(Attribute attribute) {
        for (Extension extension : attribute.getExtension()) {
            if (extension instanceof Documentation) {
                return (Documentation) extension;
            }
        }
        return null;
    }

    static Attribute findAttributeByAlias(List<Attribute> attributes, String alias) {
        for (Attribute attribute : attributes) {
            Documentation documentation = findDocumentation(attribute);
            if (documentation != null && StringUtils.equals(documentation.getAlias(), alias)) {
                return attribute;
            }
        }
        return null;
    }
}
