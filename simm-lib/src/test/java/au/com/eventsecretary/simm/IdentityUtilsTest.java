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
        assertThat(IdentityUtils.cleanPhoneNumber(""), is(""));
        assertThat(IdentityUtils.cleanPhoneNumber(" "), is(""));
        assertThat(IdentityUtils.cleanPhoneNumber("0407433955"), is("0407433955"));
        assertThat(IdentityUtils.cleanPhoneNumber("0407 433 955"), is("0407433955"));
        assertThat(IdentityUtils.cleanPhoneNumber("0407  433  955"), is("0407433955"));
        assertThat(IdentityUtils.cleanPhoneNumber("(03) 9744 4417"), is("0397444417"));
        assertThat(IdentityUtils.cleanPhoneNumber("+61407433955"), is("0407433955"));
    }
}