package au.com.eventsecretary.simm.code;

import au.com.auspost.simm.model.ComplexType;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ParseClassTest {
    public static class TestBase {
        @Input(alias="Base One")
        private String baseOne;
    }

    @Test
    public void testOne() {
        ParseClass parseClass = new ParseClass();
        List<ComplexType> complexTypeList = parseClass.parseClass(TestBase.class);
        assertThat(complexTypeList.size(), is(1));
    }
}