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

package de.apporiented.hermesftp.streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.apporiented.hermesftp.utils.IOUtils;

import org.junit.Test;

// CHECKSTYLE:OFF

/**
 * Testcase for block mode streams.
 * 
 * @author Lars Behnke
 */
public class TextStreamTest {

    /**
     * Testcase : input text stream.
     */
    @Test
    public void testLineStream() {
        try {
            String text = "ABC\nDEF\n123\n\u00C4\u00D6\u00DC";
            ByteArrayInputStream bais = new ByteArrayInputStream(text
                    .getBytes("UTF16"));
            TextInputStream lis = new TextInputStream(bais, "UTF16");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TextOutputStream los = new TextOutputStream(baos, "ISO-8859-1");
            int b;
            while ((b = lis.read()) != -1) {
                los.write(b);
            }
            los.flush();
            String destText = new String(baos.toByteArray(), "ISO-8859-1");
            assertEquals(text, destText);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Testcase : input text records.
     */
    @Test
    public void testLineRecord() {
        TextInputStream lis = null;
        TextOutputStream los = null;
        try {
            String lineSep = System.getProperty("line.separator");
            String text = "ABC" + lineSep + "DEF" + lineSep + "123" + lineSep
                    + "\u00C4\u00D6\u00DC" + lineSep;
            ByteArrayInputStream bais = new ByteArrayInputStream(text
                    .getBytes("Cp273"));
            lis = new TextInputStream(bais, "Cp273");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            los = new TextOutputStream(baos, "ISO-8859-1");
            byte[] record;
            while ((record = lis.readRecord()) != null) {
                los.writeRecord(record, false);
            }
            los.flush();
            String destText = new String(baos.toByteArray(), "ISO-8859-1");
            assertEquals(text, destText);
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            IOUtils.closeGracefully(los);
            IOUtils.closeGracefully(lis);
        }
    }

}
// CHECKSTYLE:ON
