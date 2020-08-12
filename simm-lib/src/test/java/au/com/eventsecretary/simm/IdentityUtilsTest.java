package au.com.eventsecretary.simm;

import org.junit.Test;

import java.util.Map;

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

    @Test
    public void splitName() {
        assertSplit(IdentityUtils.splitName("abc def"), "abc", "def");
        assertSplit(IdentityUtils.splitName("abc  def"), "abc", "def");
        assertSplit(IdentityUtils.splitName(" abc  def "), "abc", "def");
        assertSplit(IdentityUtils.splitName("abc"), "abc", null);
        assertSplit(IdentityUtils.splitName(" abc "), "abc", null);
        assertSplit(IdentityUtils.splitName(" abc  "), "abc", null);
        assertSplit(IdentityUtils.splitName(""), null, null);
        assertSplit(IdentityUtils.splitName(null), null, null);
    }

    private void assertSplit(String[] split, String part1, String part2) {
        if (part1 == null && part2 == null) {
            assertThat(split.length, is(0));
            return;
        }
        if (part2 == null) {
            assertThat(split.length, is(1));
            assertThat(split[0], is(part1));
            return;
        }
        assertThat(split.length, is(2));
        assertThat(split[0], is(part1));
        assertThat(split[1], is(part2));
    }

    @Test
    public void stringsToMap() {
        assertMap(IdentityUtils.stringsToMap(null), 0);
        assertMap(IdentityUtils.stringsToMap(""), 0);
        assertMap(IdentityUtils.stringsToMap("a=A,b=B,c=C"), 3, "a", "A", "b", "B", "c", "C");
    }

    private void assertMap(Map<String, String> map, int size, String... keyPair) {
        assertThat(map.size(), is(size));
        for (int i = 0; i < keyPair.length; i++) {
            assertThat(map.get(keyPair[i++]), is(keyPair[i]));
        }
    }
}