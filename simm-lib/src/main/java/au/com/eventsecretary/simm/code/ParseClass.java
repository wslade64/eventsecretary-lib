package au.com.eventsecretary.simm.code;

import au.com.auspost.simm.model.*;
import au.com.eventsecretary.UnexpectedSystemException;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ParseClass {
    Map<Class<?>, ComplexType> classMap = new HashMap<>();
    List<ComplexType> classList = new ArrayList<>();

    public static ComplexType findComplexTypeByClass(List<ComplexType> list, Class<?> clazz) {
        return findComplexTypeById(list, id(clazz));
    }

    public static ComplexType findComplexTypeById(List<ComplexType> list, String id) {
        return list.stream().filter(ct -> ct.getId().equals(id)).findFirst().orElse(null);
    }

    private ParseClass() {
    }

    private static String id(String value) {
        return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String id(Class<?> clazz) {
        return id(clazz.toString());
    }

    public static List<ComplexType> parseClass(Class<?> clazz) {
        ParseClass parseClass = new ParseClass();
        while (clazz != null) {
            parseClass.processClass(clazz);
            clazz = clazz.getSuperclass();
            if (clazz == Object.class) {
                clazz = null;
            }
        }
        return parseClass.classList;
    }

    private ComplexType processClass(Class<?> clazz) {
        List<Attribute> attributes = processAttributes(clazz);
        ComplexType complexType = new ComplexTypeImpl();
        complexType.setId(id(clazz));
        complexType.setName(clazz.getName());
        complexType.setType(Type.COMPLEX);
        complexType.getAttributes().addAll(attributes);
        classMap.put(clazz, complexType);
        classList.add(complexType);
        return complexType;
    }

    private void mapType(Field field, Attribute attribute) {
        Class<?> type = field.getType();
        if (type == String.class || type == int.class || type == Integer.class || type == boolean.class || type == Boolean.class || type == BigDecimal.class) {
            attribute.setType(Type.INTRINSIC);
            if (type == String.class) {
                attribute.setClassifier("STRING");
            } else if (type == Boolean.class || type == boolean.class) {
                attribute.setClassifier("BOOLEAN");
            } else if (type == Integer.class || type == int.class) {
                attribute.setClassifier("INTEGER");
            } else if (type == BigDecimal.class) {
                attribute.setClassifier("DECIMAL");
            }
        } else if (type.isEnum()) {
            attribute.setType(Type.ENUM);
            ComplexType complexType = classMap.get(type);
            if (complexType == null) {
                complexType = processEnum(type);
            }
            attribute.setClassifier(complexType.getId());
        } else {
          attribute.setType(type.isInterface() ? Type.ABSTRACT : Type.COMPLEX);
          ComplexType complexType = classMap.get(type);
          if (complexType == null) {
              if (type.isInterface()) {
                  complexType = processInterface(field.getType());
              } else {
                  complexType = processClass(type);
              }
          }
          attribute.setClassifier(complexType.getId());
        }
    }

    private ComplexType processInterface(Class<?> interfaceType) {
        ComplexType complexType = processClass(interfaceType);
        complexType.setType(Type.ABSTRACT);
        try {
            List<Class<?>> implementations = UniversalClassScanner.findImplementations(interfaceType, "au.com.eventsecretary");
            for (Class<?> implementation : implementations) {
                ComplexType implementationComplexType = processClass(implementation);
                implementationComplexType.setBaseClassifier(complexType.getId());
            }
            return complexType;
        } catch (Exception e) {
            throw new UnexpectedSystemException(e);
        }
    }

    private ComplexType processEnum(Class<?> enumType) {
        ComplexType complexType = new ComplexTypeImpl();
        complexType.setId(id(enumType));
        complexType.setName(enumType.getName());
        complexType.setType(Type.ENUM);
        complexType.getAttributes().addAll(processEnumAttributes(enumType));
        classMap.put(enumType, complexType);
        classList.add(complexType);
        return complexType;
    }

    private List<Attribute> processAttributes(Class<?> clazz) {
        List<Attribute> attributeList = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Input annotation = declaredField.getAnnotation(Input.class);
            if (annotation != null) {
                Attribute attribute = new AttributeImpl();
                attribute.setId(id(declaredField.toString()));
                attribute.setName(declaredField.getName());
                attribute.setMinCount(annotation.required() ? 1 : 0);
                attribute.setMaxCount(1);
                mapType(declaredField, attribute);
                List<Extension> extension = attribute.getExtension();
                Documentation documentation = new DocumentationImpl();
                documentation.setAlias(annotation.alias());
                extension.add(documentation);
                attributeList.add(attribute);
            }
        }
        return attributeList;
    }

    private List<Attribute> processEnumAttributes(Class<?> clazz) {
        List<Attribute> attributeList = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Input annotation = declaredField.getAnnotation(Input.class);
            if (annotation != null) {
                Attribute attribute = new AttributeImpl();
                attribute.setId(id(declaredField.toString()));
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
