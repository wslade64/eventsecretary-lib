package au.com.eventsecretary.export;

import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Intrinsic;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.simm.ModelExtensions;

import java.util.function.Function;

import static au.com.eventsecretary.export.ValueFormatter.enumFormatterClassifier;
import static au.com.eventsecretary.simm.ExtensionUtils.alias;
import static au.com.eventsecretary.simm.ExtensionUtils.findComplexTypeById;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface ModelUtils {
    static SheetBuilderFunction complexTypeBuilder(ModelExtensions modelExtensions, ComplexType complexType) {
        return complexTypeBuilder(modelExtensions, complexType, null);
    }
    static SheetBuilderFunction complexTypeBuilder(ModelExtensions modelExtensions, ComplexType complexType, Function<String, Boolean> include) {
        return sheetBuilder -> {
            if (complexType == null) {
                return;
            }

            complexType.getAttributes().forEach(attribute -> {
                if (include != null) {
                    if (!include.apply(attribute.getId())) {
                        return;
                    }
                }
                ColumnBuilder builder = sheetBuilder.column().label(alias(attribute));
                switch (attribute.getType()) {
                    case ENUM:
                        ComplexType enumComplexType = findComplexTypeById(modelExtensions.getComplexTypeExtensions(), attribute.getClassifier());
                        builder.stringFormat(enumFormatterClassifier(enumComplexType));
                        break;
                    case INTRINSIC:
                        if (attribute.getClassifier().equals(Intrinsic.BOOLEAN.name())) {
                            builder.booleanFormat();
                        } else if (attribute.getClassifier().equals(Intrinsic.STRING.name())) {
                            builder.stringFormat();
                        } else if (attribute.getClassifier().equals(Intrinsic.INTEGER.name())) {
                            builder.integerFormat();
                        } else if (attribute.getClassifier().equals(Intrinsic.DECIMAL.name())) {
                            builder.numericFormat();
                        } else {
                            throw new UnexpectedSystemException("IntrinsicNotSupported:" + attribute.getClassifier());
                        }
                        break;
                    default:
                        throw new UnexpectedSystemException("TypeNotSupported:" + attribute.getType());
                }
                builder.end();
            });
        };
    }
}
