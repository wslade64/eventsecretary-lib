package au.com.eventsecretary.simm;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Documentation;
import au.com.auspost.simm.model.Extension;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface ExtensionUtils {
    static String alias(Attribute attribute) {
        Documentation documentation = documentation(attribute);
        return documentation != null ? documentation.getAlias() : attribute.getName();
    }

    static String note(Attribute attribute) {
        Documentation documentation = documentation(attribute);
        return documentation != null ? documentation.getNote() : null;
    }

    static ComplexType findComplexTypeById(List<ComplexType> complexTypeList, String id) {
        return complexTypeList.stream().filter(complexType -> complexType.getId().equals(id)).findFirst().get();
    }

    static Documentation documentation(Attribute attribute) {
        for (Extension extension : attribute.getExtension()) {
            if (extension instanceof Documentation) {
                return (Documentation)extension;
            }
        }
        return null;
    }
}
