package au.com.eventsecretary.builder;

import au.com.auspost.simm.constraint.HorizontalSize;
import au.com.auspost.simm.constraint.Presentation;
import au.com.auspost.simm.constraint.PresentationImpl;
import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.AttributeImpl;
import au.com.auspost.simm.model.BaseType;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.ComplexTypeImpl;
import au.com.auspost.simm.model.Documentation;
import au.com.auspost.simm.model.DocumentationImpl;
import au.com.auspost.simm.model.Extension;
import au.com.auspost.simm.model.Intrinsic;
import au.com.auspost.simm.model.Type;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.simm.ModelExtensions;

import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ComplexTypeBuilder<P> {
    final ComplexType complexType;
    final P eventBuilder;
    final ModelExtensions modelExtensions;

    public ComplexTypeBuilder(String id, ModelExtensions modelExtensions, P eventBuilder) {
        complexType = new ComplexTypeImpl();
        complexType.setId(id);
        this.eventBuilder = eventBuilder;
        this.modelExtensions = modelExtensions;
        modelExtensions.getComplexTypeExtensions().add(complexType);
    }

    public ComplexTypeBuilder<P> name(String name) {
        this.complexType.setName(name);
        return this;
    }
    protected Attribute attribute() {
        Attribute attribute = new AttributeImpl();
        attribute.setId(id());
        attribute.setName(attribute.getId());
        attribute.setMinCount(1);
        complexType.getAttributes().add(attribute);
        return attribute;
    }

    static <T extends Extension> T extension(BaseType baseType, Class<T> tClass) {
        for (Extension extension : baseType.getExtension()) {
            if (extension.getClass().equals(tClass)) {
                return (T)extension;
            }
        }
        try {
            T t = tClass.newInstance();
            baseType.getExtension().add(t);
            return t;
        } catch (InstantiationException e) {
            throw new UnexpectedSystemException(e);
        } catch (IllegalAccessException e) {
            throw new UnexpectedSystemException(e);
        }
    }

    protected Documentation documentation(Attribute attribute, String alias, String note) {
        Documentation documentation = new DocumentationImpl();
        documentation.setAlias(alias);
        documentation.setNote(note);
        attribute.getExtension().add(documentation);
        return documentation;
    }

    public ComplexTypeBuilder<P> enumAttribute$(String alias, String... enumValues) {
        Attribute attribute = attribute();
        attribute.setType(Type.ENUM);

        if (alias != null) {
            documentation(attribute, alias, null);
        }

        ComplexType enumComplexType = new ComplexTypeImpl();
        enumComplexType.setId(id());
        enumComplexType.setType(Type.ENUM);
        modelExtensions.getComplexTypeExtensions().add(enumComplexType);

        for (int i = 0; i < enumValues.length; i++) {
            Attribute enumAttribute = new AttributeImpl();
            enumAttribute.setId(id());
            enumAttribute.setName(enumAttribute.getId());
            documentation(enumAttribute, enumValues[i], null);
            enumComplexType.getAttributes().add(enumAttribute);
        }

        attribute.setClassifier(enumComplexType.getId());

        return this;
    }

    public AttributeBuilder enumAttribute(String alias, String... enumValues) {
        enumAttribute$(alias, enumValues);
        return new AttributeBuilder(complexType.getAttributes().get(complexType.getAttributes().size() - 1));
    }

    public ComplexTypeBuilder<P> booleanAttribute$(String alias) {
        Attribute attribute = attribute();
        attribute.setType(Type.INTRINSIC);
        attribute.setClassifier(Intrinsic.BOOLEAN.toString());
        if (alias != null) {
            documentation(attribute, alias, null);
        }
        return this;
    }

    public ComplexTypeBuilder<P> textAttribute$(String alias) {
        Attribute attribute = attribute();
        attribute.setType(Type.INTRINSIC);
        attribute.setClassifier(Intrinsic.STRING.toString());
        if (alias != null) {
            documentation(attribute, alias, null);
        }
        return this;
    }

    public AttributeBuilder textAttribute(String alias) {
        textAttribute$(alias);
        return new AttributeBuilder(complexType.getAttributes().get(complexType.getAttributes().size() - 1));
    }

    public ComplexTypeBuilder<P> alias(String alias) {
        extension(complexType, DocumentationImpl.class).setAlias(alias);
        return this;
    }

    public ComplexTypeBuilder<P> note(String note) {
        extension(complexType, DocumentationImpl.class).setNote(note);
        return this;
    }

    public class AttributeBuilder {
        final Attribute attribute;

        AttributeBuilder(Attribute attribute) {
            this.attribute = attribute;
        }

        public AttributeBuilder size(HorizontalSize size) {
            Presentation extension = extension(attribute, PresentationImpl.class);
            extension.setSize(size);
            return this;
        }

        public AttributeBuilder optional() {
            attribute.setMinCount(0);
            return this;
        }

        public ComplexTypeBuilder<P> end() {
            return ComplexTypeBuilder.this;
        }

    }

    public P end() {
        return this.eventBuilder;
    }

}
