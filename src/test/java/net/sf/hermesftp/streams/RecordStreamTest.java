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

package net.sf.hermesftp.streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

// CHECKSTYLE:OFF

/**
 * Test cases for record stream classes.
 * 
 * @author Lars Behnke
 */
public class RecordStreamTest extends TestCase {

    /**
     * Test case: reading a record stream.
     */
    public void testReadRecordStream() {
        try {
            byte[] data = createRecordData();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream bais = new ByteArrayInputStream(data);
            RecordInputStream ris = new RecordInputStream(bais, new byte[] {10, 13});
            int b;
            while ((b = ris.read()) != -1) {
                baos.write(b);
            }
            ris.close();
            baos.close();
            byte[] resultBytes = baos.toByteArray();
            assertEquals(13, resultBytes.length);
            assertEquals(65, resultBytes[0]);
            assertEquals(66, resultBytes[1]);
            assertEquals(67, resultBytes[2]);
            assertEquals(10, resultBytes[3]);
            assertEquals(13, resultBytes[4]);
            assertEquals(68, resultBytes[5]);
            assertEquals(69, resultBytes[6]);
            assertEquals(70, resultBytes[7]);
            assertEquals(10, resultBytes[8]);
            assertEquals(13, resultBytes[9]);
            assertEquals(255, resultBytes[10] & 255);
            assertEquals(10, resultBytes[11]);
            assertEquals(13, resultBytes[12]);
        } catch (IOException e) {
            fail(e.toString());
        }

        try {
            byte[] data = createRecordData();
            InputStream bais = new ByteArrayInputStream(data);
            RecordInputStream ris = new RecordInputStream(bais, new byte[] {10, 13});
            byte[] record;
            record = ris.readRecord();
            assertEquals("ABC", new String(record, "ASCII"));
            record = ris.readRecord();
            assertEquals("DEF", new String(record, "ASCII"));
            record = ris.readRecord();
            assertEquals(255, record[0] & 255);
            record = ris.readRecord();
            assertNull(record);
        } catch (IOException e) {
            fail(e.toString());
        }

    }

    /**
     * Test case: writing records.
     */
    public void testWriteRecordStream() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RecordOutputStream ros = new RecordOutputStream(baos);
            ros.write("ABC".getBytes("ASCII"));
            ros.finalizeRecord(false);
            ros.write("DEF".getBytes("ASCII"));
            ros.finalizeRecord(false);
            ros.write(255);
            ros.finalizeRecord(true);
            ros.close();

            byte[] resultBytes = baos.toByteArray();

            assertEquals(14, resultBytes.length);
            assertEquals(65, resultBytes[0]);
            assertEquals(66, resultBytes[1]);
            assertEquals(67, resultBytes[2]);
            assertEquals(255, resultBytes[3] & 255);
            assertEquals(1, resultBytes[4]);
            assertEquals(68, resultBytes[5]);
            assertEquals(69, resultBytes[6]);
            assertEquals(70, resultBytes[7]);
            assertEquals(255, resultBytes[8] & 255);
            assertEquals(1, resultBytes[9]);
            assertEquals(255, resultBytes[10] & 255);
            assertEquals(255, resultBytes[11] & 255);
            assertEquals(255, resultBytes[12] & 255);
            assertEquals(3, resultBytes[13]);

        } catch (IOException e) {
            fail(e.toString());
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            RecordOutputStream ros = new RecordOutputStream(baos);
            ros.writeRecord("ABC".getBytes("ASCII"), false);
            ros.writeRecord("DEF".getBytes("ASCII"), false);
            ros.writeRecord(new byte[] {(byte) 255}, true);
            ros.close();

            byte[] resultBytes = baos.toByteArray();

            assertEquals(14, resultBytes.length);
            assertEquals(65, resultBytes[0]);
            assertEquals(66, resultBytes[1]);
            assertEquals(67, resultBytes[2]);
            assertEquals(255, resultBytes[3] & 255);
            assertEquals(1, resultBytes[4]);
            assertEquals(68, resultBytes[5]);
            assertEquals(69, resultBytes[6]);
            assertEquals(70, resultBytes[7]);
            assertEquals(255, resultBytes[8] & 255);
            assertEquals(1, resultBytes[9]);
            assertEquals(255, resultBytes[10] & 255);
            assertEquals(255, resultBytes[11] & 255);
            assertEquals(255, resultBytes[12] & 255);
            assertEquals(3, resultBytes[13]);

        } catch (IOException e) {
            fail(e.toString());
        }
    }

    private byte[] createRecordData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(65);
        baos.write(66);
        baos.write(67);
        baos.write(255);
        baos.write(1);
        baos.write(68);
        baos.write(69);
        baos.write(70);
        baos.write(255);
        baos.write(1);
        baos.write(255);
        baos.write(255);
        baos.write(255);
        baos.write(3);
        return baos.toByteArray();
    }
}

// CHECKSTYLE:ON

