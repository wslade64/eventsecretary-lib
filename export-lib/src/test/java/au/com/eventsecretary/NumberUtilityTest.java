package au.com.eventsecretary;

import org.junit.Test;

import java.math.BigDecimal;

import static au.com.eventsecretary.NumberUtility.isNegative;
import static au.com.eventsecretary.NumberUtility.isPositive;
import static au.com.eventsecretary.NumberUtility.isZero;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class NumberUtilityTest {
    @Test
    public void testIs() {
        assertThat(isZero(new BigDecimal("0")), is(true));
        assertThat(isZero(new BigDecimal("0.1")), is(false));
        assertThat(isPositive(new BigDecimal("1.01")), is(true));
        assertThat(isPositive(new BigDecimal("-1.01")), is(false));
        assertThat(isNegative(new BigDecimal("-1.01")), is(true));
        assertThat(isNegative(new BigDecimal("1.01")), is(false));
    }

}