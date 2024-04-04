package au.com.eventsecretary;

import org.junit.Test;

import java.math.BigDecimal;

import static au.com.eventsecretary.NumberUtility.*;
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

        assertThat(trimDecimal(""), is(""));
        assertThat(trimDecimal("0.000"), is("0"));
        assertThat(trimDecimal("1"), is("1"));
        assertThat(trimDecimal("1.001"), is("1.001"));
        assertThat(trimDecimal("1.123"), is("1.123"));
        assertThat(trimDecimal("1.010"), is("1.01"));
        assertThat(trimDecimal("1.100"), is("1.1"));
        assertThat(trimDecimal("1.000"), is("1"));

        assertThat(rate(300, 300), is(60));
        assertThat(rate(301, 300), is(61));
        assertThat(rate(349, 300), is(70));
    }

}