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
    
//    public void testRegex() {
//        boolean result;
//        
//
//        Pattern pattern = Pattern.compile("^.*\\(\\|\\|\\|([0-9]+)\\|\\).*$");
//        Matcher matcher = pattern.matcher("229 Entering Extended Passive Mode (|||12000|)");
//   
//        if (matcher.matches()) {
//            Integer.parseInt(matcher.group(1));
//        } else {
//            fail();
//        }
//
//    }
}
