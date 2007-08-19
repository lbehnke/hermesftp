/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2007 Lars Behnke
 * ------------------------------------------------------------------------------
 * 
 * This file is part of Hermes FTP Server.
 * 
 * Hermes FTP Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Hermes FTP Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Hermes FTP Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * ------------------------------------------------------------------------------
 */

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

    // public void testRegex() {
    // boolean result;
    //        
    //
    // Pattern pattern = Pattern.compile("^.*\\(\\|\\|\\|([0-9]+)\\|\\).*$");
    // Matcher matcher = pattern.matcher("229 Entering Extended Passive Mode (|||12000|)");
    //   
    // if (matcher.matches()) {
    // Integer.parseInt(matcher.group(1));
    // } else {
    // fail();
    // }
    //
    // }
}
