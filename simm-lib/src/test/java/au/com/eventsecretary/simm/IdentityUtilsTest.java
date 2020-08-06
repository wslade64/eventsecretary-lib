package au.com.eventsecretary.simm;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class IdentityUtilsTest {

    @Test
    public void cleanPhoneNumber() {
        assertThat(IdentityUtils.cleanPhoneNumber(null), is(nullValue()));
    }
}