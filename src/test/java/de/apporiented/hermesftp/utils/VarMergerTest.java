/*
 * ------------------------------------------------------------------------------
 * Hermes FTP Server
 * Copyright (c) 2005-2014 Lars Behnke
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

package de.apporiented.hermesftp.utils;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class VarMergerTest  {

    @Test
    public void testMerge() {
        VarMerger vm = new VarMerger("ABC ${abc} DEF ${def} GHI ${ghi}");
        Properties props = new Properties();
        props.put("abc", "ABC");
        props.put("def", "\\");
        props.put("ghi", "$");
        vm.merge(props);
        String result = vm.getText();
        assertEquals("ABC ABC DEF \\ GHI $", result);
    }
}
