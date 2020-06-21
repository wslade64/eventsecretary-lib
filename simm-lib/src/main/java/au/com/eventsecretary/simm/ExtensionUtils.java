package au.com.eventsecretary.simm;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.Documentation;
import au.com.auspost.simm.model.Extension;

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

    static Documentation documentation(Attribute attribute) {
        for (Extension extension : attribute.getExtension()) {
            if (extension instanceof Documentation) {
                return (Documentation)extension;
            }
        }
        return null;
    }
}
