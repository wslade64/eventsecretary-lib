package au.com.eventsecretary.simm;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Type;

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
}
