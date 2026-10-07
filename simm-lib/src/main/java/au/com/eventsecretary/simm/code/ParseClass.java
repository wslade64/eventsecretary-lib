package au.com.eventsecretary.simm.code;

import au.com.auspost.simm.model.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ParseClass {
    public List<ComplexType> parseClass(Class clazz) {
        List<ComplexType> complexTypeList = new ArrayList<>();
        while (clazz != null) {
            ComplexType complexType = processClass(clazz);
            if (complexType != null) {
                complexTypeList.add(complexType);
            }
            clazz = clazz.getSuperclass();
        }
        return complexTypeList;
    }

    private ComplexType processClass(Class clazz) {
        List<Attribute> attributes = findAttributes(clazz);
        if (attributes.isEmpty()) {
            return null;
        }
        ComplexType complexType = new ComplexTypeImpl();
        complexType.setName(clazz.getName());
        complexType.getAttributes().addAll(attributes);

        return complexType;
    }

    private List<Attribute> findAttributes(Class clazz) {
        List<Attribute> attributeList = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Input annotation = declaredField.getAnnotation(Input.class);
            if (annotation != null) {
                Attribute attribute = new AttributeImpl();
                attribute.setName(declaredField.getName());
                List<Extension> extension = attribute.getExtension();
                Documentation documentation = new DocumentationImpl();
                documentation.setAlias(annotation.alias());
                extension.add(documentation);
                attributeList.add(attribute);
            }
        }
        return attributeList;
    }
}
