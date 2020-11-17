package au.com.eventsecretary.export;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RowBuilderTest {
    @Test
    public void columnNames() {
        assertThat(RowBuilder.formatColumn(0), is("A"));
        assertThat(RowBuilder.formatColumn(25), is("Z"));
        assertThat(RowBuilder.formatColumn(26), is("AA"));
        assertThat(RowBuilder.formatColumn(51), is("AZ"));
        assertThat(RowBuilder.formatColumn(52), is("BA"));
        assertThat(RowBuilder.formatColumn(77), is("BZ"));
    }
}