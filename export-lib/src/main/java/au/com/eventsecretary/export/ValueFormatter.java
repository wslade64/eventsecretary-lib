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

    static ValueFormatter<Enum, String> enumFormatter(MetadataModelService metadataModelService, String attributeId) {
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

    static ValueFormatter<Enum, String> enumFormatterClassifier(ComplexType enumComplexType) {
        return value -> {
            for (Attribute attribute : enumComplexType.getAttributes()) {
                if (attribute.getName().equals(value.name())) {
                    return alias(attribute);
                }
            }
            return value.name();
        };
    }

}
