package au.com.eventsecretary.export;

import au.com.auspost.simm.acv.service.MetadataModelService;
import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.eventsecretary.UnexpectedSystemException;

import java.util.Optional;

import static au.com.auspost.simm.acv.service.ModelUtil.fetchAttributeByName;
import static au.com.eventsecretary.simm.ExtensionUtils.alias;

public interface ValueFormatter<S, T> {
    T format(S value);

    static ValueFormatter<Object, String> enumFormatter(MetadataModelService metadataModelService, String attributeId) {
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

        return enumFormatterClassifier(optionalEnumComplexType.get());
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
