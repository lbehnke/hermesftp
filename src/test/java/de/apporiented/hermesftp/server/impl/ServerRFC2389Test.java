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
package de.apporiented.hermesftp.server.impl;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

// CHECKSTYLE:OFF

/**
 * Unit tests that cover RFC 2389 functionality.
 * 
 * @author Lars Behnke
 */
public class ServerRFC2389Test extends AbstractClientServerTestCase {

    /**
     * Test case: Feature negotiation.
     */
    @Test public void testFeatureNegociation() {
        try {
            String str;
            str = getClient().sendAndReceive("FEAT");
            assertTrue(str.indexOf("UTF8") > 0);

            // TODO test RFC 2389 commands

        } catch (IOException e) {
            fail(e.toString());
        }
    }

}

// CHECKSTYLE:ON

