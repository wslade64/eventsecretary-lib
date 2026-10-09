package au.com.eventsecretary.simm.code;

import au.com.auspost.simm.model.Attribute;
import au.com.auspost.simm.model.ComplexType;
import au.com.auspost.simm.model.Documentation;
import au.com.auspost.simm.model.Type;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static au.com.eventsecretary.simm.ModelUtils.findDocumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseClassTest {
    enum TestEnum {
        @Input(alias="Value one")
        valueOne,
        @Input(alias="Value two")
        valueTwo,
        @Input(alias="Value three")
        valueThree
    }

    public static class TestBase {
        @Input(alias="String", required = false)
        private String stringValue;
        @Input(alias="Integer")
        private int intValue;
        @Input(alias="Boolean")
        private boolean booleanValue;
        @Input(alias="BigDecimal")
        private BigDecimal decimalValue;
    }

    public static class TestEnumClass {
        @Input(alias="Enum")
        private TestEnum enumValue;
    }

    public static class TestDerived extends TestBase{
        @Input(alias="Sub One", required = false)
        private String subOne;
    }

    public static interface TestInterface {
        void testFunction();
    }

    public static class FunctionOne implements TestInterface {
        @Override
        public void testFunction() {
        }
    }

    public static class FunctionTwo implements TestInterface {
        @Override
        public void testFunction() {
        }
    }

    public static class Composite {
        @Input(alias="test1")
        private TestInterface test1;
    }

    @Test
    public void testOne() {
        List<ComplexType> complexTypeList = ParseClass.parseClass(TestBase.class);
        assertThat(complexTypeList.size(), is(1));

        ComplexType complexType = complexTypeList.getFirst();
        assertThat(complexType.getAttributes().size(), is(4));

        Attribute first = complexType.getAttributes().getFirst();
        assertThat(first.getType(), is(Type.INTRINSIC));
        assertThat(first.getClassifier(), is("STRING"));
        assertThat(first.getName(), is("stringValue"));

        assertThat(first.getMinCount(), is(0));
        assertThat(first.getMaxCount(), is(1));

        Documentation documentation = findDocumentation(first);
        assertThat(documentation, is(notNullValue()));
        assertThat(documentation.getAlias(), is("String"));

        Attribute second = complexType.getAttributes().get(1);
        assertThat(second.getType(), is(Type.INTRINSIC));
        assertThat(second.getClassifier(), is("INTEGER"));
        assertThat(second.getName(), is("intValue"));

        assertThat(second.getMinCount(), is(1));
        assertThat(second.getMaxCount(), is(1));

        Attribute third = complexType.getAttributes().get(2);
        assertThat(third.getType(), is(Type.INTRINSIC));
        assertThat(third.getClassifier(), is("BOOLEAN"));
        assertThat(third.getName(), is("booleanValue"));

        Attribute fourth = complexType.getAttributes().get(3);
        assertThat(fourth.getType(), is(Type.INTRINSIC));
        assertThat(fourth.getClassifier(), is("DECIMAL"));
        assertThat(fourth.getName(), is("decimalValue"));
    }

    @Test
    public void testEnum() {
        List<ComplexType> complexTypeList = ParseClass.parseClass(TestEnumClass.class);
        assertThat(complexTypeList.size(), is(2));

        ComplexType testEnumClass = ParseClass.findComplexTypeByClass(complexTypeList, TestEnumClass.class);
        assertThat(testEnumClass, is(notNullValue()));

        assertThat(testEnumClass.getAttributes().size(), is(1));

        Attribute first = testEnumClass.getAttributes().getFirst();
        assertThat(first.getType(), is(Type.ENUM));
        assertThat(first.getName(), is("enumValue"));
        String firstClassifier = first.getClassifier();
        assertThat(firstClassifier, is(notNullValue()));

        ComplexType fifthComplexType = ParseClass.findComplexTypeById(complexTypeList, firstClassifier);
        assertThat(fifthComplexType, is(notNullValue()));

        assertThat(fifthComplexType.getAttributes().size(), is(3));
        Attribute enumFirst = fifthComplexType.getAttributes().getFirst();
        assertThat(enumFirst.getName(), is("valueOne"));
        assertThat(enumFirst.getId(), is(notNullValue()));
        Documentation documentationEnum = findDocumentation(enumFirst);
        assertThat(documentationEnum.getAlias(), is("Value one"));
    }

    @Test
    public void testDerived() {
        List<ComplexType> complexTypeList = ParseClass.parseClass(TestDerived.class);

        assertThat(complexTypeList.size(), is(2));
    }

    @Test
    public void testComposite() {
        List<ComplexType> complexTypeList = ParseClass.parseClass(Composite.class);
        assertThat(complexTypeList.size(), is(4));

        ComplexType complexType = ParseClass.findComplexTypeByClass(complexTypeList, Composite.class);
        assertThat(complexType, is(notNullValue()));

        List<Attribute> attributes = complexType.getAttributes();
        assertThat(attributes.size(), is(1));

        Attribute first = attributes.getFirst();
        assertThat(first.getType(), is(Type.ABSTRACT));

        ComplexType interfaceComplexType = ParseClass.findComplexTypeByClass(complexTypeList, TestInterface.class);
        assertThat(interfaceComplexType, is(notNullValue()));

        assertThat(first.getClassifier(), is(interfaceComplexType.getId()));

        ComplexType oneComplexType = ParseClass.findComplexTypeByClass(complexTypeList, FunctionOne.class);
        assertThat(oneComplexType, is(notNullValue()));
        assertThat(oneComplexType.getBaseClassifier(), is(interfaceComplexType.getId()));

        ComplexType twoComplexType = ParseClass.findComplexTypeByClass(complexTypeList, FunctionTwo.class);
        assertThat(twoComplexType, is(notNullValue()));
        assertThat(twoComplexType.getBaseClassifier(), is(interfaceComplexType.getId()));
    }
}