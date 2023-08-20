package au.com.eventsecretary.export;

import au.com.auspost.simm.acv.service.MetadataModelService;
import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.eventsecretary.UnexpectedSystemException;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static au.com.auspost.simm.acv.service.ModelUtil.fetchAttributeByName;
import static au.com.eventsecretary.simm.ExtensionUtils.alias;

public interface ValueFormatter<S, T> {
    T format(S value);

    static String enumParser(MetadataModelService metadataModelService, String attributeId, String enumAlias) {
        if (enumAlias == null) {
            return null;
        }
        ComplexType complexType = enumComplexType(metadataModelService, attributeId);
        for (Attribute attribute : complexType.getAttributes()) {
            if (StringUtils.equals(enumAlias, alias(attribute))) {
                return attribute.getName();
            }
        }
        throw new UnexpectedSystemException("InvalidEnum:"+enumAlias);
    }

    static ComplexType enumComplexType(MetadataModelService metadataModelService, String attributeId) {
        String[] split = attributeId.split(":");
        String complexTypeId = split[0];
        String attributeName = split[1];

        Optional<ComplexType> optionalComplexType = metadataModelService.fetchComplexTypeById(complexTypeId);
        if (!optionalComplexType.isPresent()) {
            throw new UnexpectedSystemException(complexTypeId);
        }
        ComplexType complexType = optionalComplexType.get();
        Optional<Attribute> optionalAttribute = fetchAttributeByName(complexType.getAttributes(), attributeName);
        if (!optionalAttribute.isPresent()) {
            throw new UnexpectedSystemException(attributeName);
        }
        String classifier = optionalAttribute.get().getClassifier();
        Optional<ComplexType> optionalEnumComplexType = metadataModelService.fetchComplexTypeById(classifier);
        if (!optionalEnumComplexType.isPresent()) {
            throw new UnexpectedSystemException(classifier);
        }
        return optionalEnumComplexType.get();
    }

    static ValueFormatter<Object, String> enumFormatter(MetadataModelService metadataModelService, String attributeId) {
        return enumFormatterClassifier(enumComplexType(metadataModelService, attributeId));
    }

    static ValueFormatter<Object, String> enumFormatterClassifier(MetadataModelService metadataModelService, String complexTypeId) {
        Optional<ComplexType> optionalComplexType = metadataModelService.fetchComplexTypeById(complexTypeId);
        if (!optionalComplexType.isPresent()) {
            throw new UnexpectedSystemException(complexTypeId);
        }
        return enumFormatterClassifier(optionalComplexType.get());
    }

    static ValueFormatter<Object, String> enumFormatterClassifier(ComplexType enumComplexType) {
        return value -> {
            if (value == null) {
                return "";
            }
            String svalue = value instanceof Enum ? ((Enum)value).name() : value.toString();

            for (Attribute attribute : enumComplexType.getAttributes()) {
                if (attribute.getName().equals(svalue)) {
                    return alias(attribute);
                }
            }
            return svalue;
        };
    }

}
