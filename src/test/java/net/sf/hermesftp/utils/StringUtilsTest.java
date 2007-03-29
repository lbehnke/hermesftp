package net.sf.hermesftp.utils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    public void testValidateEmail() {
        boolean result;
        result = StringUtils.validateEmail("lbehnke@users.sourceforge.net");
        assertTrue(result);
        result = StringUtils.validateEmail("lbehnke AT users.sourceforge.net");
        assertFalse(result);
        result = StringUtils.validateEmail("lbehnke@users@sourceforge.net");
        assertFalse(result);
        result = StringUtils.validateEmail("lars behnke@users.sourceforge.net");
        assertFalse(result);
    }
}
