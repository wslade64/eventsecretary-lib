package au.com.eventsecretary.security;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EncryptionTest {
    private static String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDLqj9Voagxl3lp5ulM0go726M01LzCP5fXK+lGo3Hgf4JG6cOVpoT4pAspOwaPWFi7JUkA1D/lI9d2YZUe2CUZI+JnzZuIG3MtV8A5VR54LasgwQnF4GoDm/bjgYaQtvthuedfuo5NmUlDm0bwRZWw6D3BAk1P8fN0WxOCgr03wqw18BXsQnkw4TOAjNn5suZdRWLusQZpB9UxeJha5p5Sy3nGq6cMK+0moMHJQROpaiPrrrq6HNdzi6cx0n/1ew1pNJumMVFBP5AN/CLVm4g2y7XGd3OudgXnwSKwU+B/twY6CavZHv7Ru6o7OQraD/C/RzunMgjVVNLue9kDMAihAgMBAAECgf9ayuqxIf/5G63kqCwZMwmc5a08QBpl/jOSmODFvNdnXXCWbF/ktvdQYNEY8FdcT+Y+h8Cjvfs+C1xiSd5QgcsIChdEb0SGyLXv4M6wwGF08eb4ExZoQEMCmOTXR0bV/LzUNCwRYPlPXqZqnS5irqKs2tusz3nnydTeajuoUuBHmpUkYXx0938gN/zUId+A7valck1OkMOJ9KN7/1IaY+5FG1mWmbeglw90gBANzGrTCHtCjQC7w6Yz1OuvpE0lKOvxC5FjvkItBPsDIZ76g3oE05Zpv8EeoA18YjHcs4BKOsMozkhPUkgWafzcM6FOglnQaOenSFssX6gZIJG3FNkCgYEA9+pFjc9ID3bGOkE4PCEGw5frEuKX9rxsnBkt4KBkqLIwgtrSjUS17cMpI9HPRw4OzfkwX7QI7tzduN0GR0hxNsHVsHz3J/2QHIuDGoMbHFbiDZS4xSw8p0IC9EEmoGDHaIOdDXX9u4JYpgkesCeNv/nZt1/zFHRS04qsMCjDOskCgYEA0k6NXQQg7m0TJgEYKOW5Y1z3ut6w4FV45awI+1vSW1TQiAIcRUpGwDLStI6bpIPqNiqAAleiVOU7iMncyvV3mmwAZ4WexVhfMCTZwAOweCOmH6pmiumt6Fot7xMpEifztGStPWRMhNifIdz/vRjSB6tTyjwrlfwcdKhujFN/cxkCgYEAkAYIfDbUPS+aP1OOUNg67rw9842yDMQwMIssLNfhQmOqbQqk0S7+pe7/4nBMA5J2JVDDuoWDvwiOwouczyKVquL2un4Os6vJ4dtmwasyv8b1skAZC2sEnYJq5u1GfFtc3SnAvoYybpFt4J9htaJ05fdHdRGHzPU6/OwN1jiIGokCgYALj9v/uCFjWkXKOhcs8Gw/xTWtZV7OBoTuA4Y+425uLPyF7a/GM24uVNz2pjxyT/pJXg7Jki3PgwbB328vDU21BKkFB/iRmukoyW3bO5ixEiyo4wJeMrin5IVP9nq0j9O7ICnSDTiTuSEOiIrax4mMeGPT43j19vVNiJEDT/fayQKBgQDbWM1CHDzT+lBj+4vnOeI980StLlagSYDhKbOXlmH2rxKDbLCuTp0phWbT8U6oJ3/JEtRC6eBLVDcDuEHL2H7tU8+QA/ub/tjqYB0gan9e/v6si05P8N9liwmIbZ91VWBejaUBcPdt/JG3UFcJMkfoZ+/AgUPrqlNWyXy+6p+/8A==";
    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy6o/VaGoMZd5aebpTNIKO9ujNNS8wj+X1yvpRqNx4H+CRunDlaaE+KQLKTsGj1hYuyVJANQ/5SPXdmGVHtglGSPiZ82biBtzLVfAOVUeeC2rIMEJxeBqA5v244GGkLb7YbnnX7qOTZlJQ5tG8EWVsOg9wQJNT/HzdFsTgoK9N8KsNfAV7EJ5MOEzgIzZ+bLmXUVi7rEGaQfVMXiYWuaeUst5xqunDCvtJqDByUETqWoj6666uhzXc4unMdJ/9XsNaTSbpjFRQT+QDfwi1ZuINsu1xndzrnYF58EisFPgf7cGOgmr2R7+0buqOzkK2g/wv0c7pzII1VTS7nvZAzAIoQIDAQAB";

    @Test
    public void testEncryption() throws Exception {
        Encryption encryption = new Encryption(publicKey, privateKey);

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 200; i++) {
            stringBuffer.append("Hello World");
        }
        String message = stringBuffer.toString();

        String encrypted = encryption.encrypt(message);
        String decrypted = encryption.decrypt(encrypted);

        assertThat(decrypted, is(message));
    }
}
