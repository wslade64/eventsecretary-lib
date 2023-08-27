package au.com.eventsecretary.simm;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class NumberUtilsTest {
    @Test
    public void isWholeNumber() {
        assertThat(NumberUtils.isWholeNumber(new BigDecimal("10")), is(true));
        assertThat(NumberUtils.isWholeNumber(new BigDecimal("10.0")), is(true));
        assertThat(NumberUtils.isWholeNumber(new BigDecimal("10.00")), is(true));
        assertThat(NumberUtils.isWholeNumber(new BigDecimal("10.01")), is(false));
    }

    @Test
    public void formatCurrency() {
        assertThat(NumberUtils.formatCurrency(new BigDecimal("1")), is("$1"));
        assertThat(NumberUtils.formatCurrency(new BigDecimal("1.99")), is("$1.99"));
        assertThat(NumberUtils.formatCurrency(new BigDecimal("1.9")), is("$1.90"));
        assertThat(NumberUtils.formatCurrency(new BigDecimal("1000")), is("$1,000"));
        assertThat(NumberUtils.formatCurrency(new BigDecimal("1000000")), is("$1,000,000"));
    }
}
