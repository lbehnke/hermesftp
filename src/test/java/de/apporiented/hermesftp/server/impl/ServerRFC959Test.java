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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import de.apporiented.hermesftp.streams.BlockModeConstants;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

// CHECKSTYLE:OFF

/**
 * Test case that covers RFC 959 functionality.
 * 
 * @author Lars Behnke
 */
public class ServerRFC959Test extends AbstractClientServerTestCase {

    private static Log log      = LogFactory.getLog(ServerRFC959Test.class);

    private String     testFile = "test.txt";

    private String     testText = "ABC \u00C4\u00D6\u00DC 123";

    /**
     * Test case: Rename and size.
     */
    @Test
    public void testRenameAndSize() {
        String str;
        try {
            getClient().sendAndReceive("DELE text.renamed");
            getClient().sendAndReceive("TYPE A");
            getClient().storeText(testFile, testText);
            str = getClient().sendAndReceive("RNFR " + testFile);
            assertTrue(str.startsWith("350"));
            str = getClient().sendAndReceive("RNTO text.renamed");
            assertTrue(str.startsWith("200"));
            str = getClient().sendAndReceive("SIZE text.renamed").trim();
            assertTrue(str.startsWith("213"));
            String[] parts = str.split(" ");
            Integer.parseInt(parts[1]);
            str = getClient().sendAndReceive("RNFR text.renamed");
            assertTrue(str.startsWith("350"));
            str = getClient().sendAndReceive("RNTO " + testFile);
            assertTrue(str.startsWith("200"));
            str = getClient().sendAndReceive("SIZE text.renamed");
            assertTrue(str.startsWith("550"));
            getClient().sendAndReceive("DELE " + testFile);
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: Folder creation, deletion and navigation.
     */
    @Test
    public void testFolderHandling() {
        String str;
        try {

            getClient().sendAndReceive("RMD newtestfolder");
            str = getClient().sendAndReceive("MKD newtestfolder");
            assertTrue(str.startsWith("250"));
            getClient().list();
            str = getClient().getTextData();
            assertTrue(str.indexOf("newtestfolder") > 0);
            str = getClient().sendAndReceive("CWD newtestfolder");
            str = getClient().sendAndReceive("PWD");
            assertTrue(str.indexOf("newtestfolder") > 0);
            str = getClient().sendAndReceive("CDUP");
            str = getClient().sendAndReceive("PWD");
            assertFalse(str.indexOf("newtestfolder") > 0);
            str = getClient().sendAndReceive("RMD newtestfolder");
            assertTrue(str.startsWith("250"));
            getClient().list();
            str = getClient().getTextData();
            assertFalse(str.indexOf("newtestfolder") > 0);
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: System commands.
     */
    @Test
    public void testSystem() {
        String str;
        try {
            str = getClient().sendAndReceive("SYST");
            assertTrue(str.indexOf("Hermes") > 0);
            str = getClient().sendAndReceive("TYPE A");
            assertTrue(str.indexOf("ASCII") > 0);
            str = getClient().sendAndReceive("TYPE E");
            assertTrue(str.indexOf("EBCDIC") > 0);
            str = getClient().sendAndReceive("TYPE I");
            assertTrue(str.indexOf("BINARY") > 0);
            str = getClient().sendAndReceive("HELP");
            assertTrue(str.startsWith("214"));
            str = getClient().sendAndReceive("ALLO 100");
            assertTrue(str.startsWith("200"));
            getClient().sendAndReceive("CLNT TestClient");
            assertTrue(str.startsWith("200"));
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: Statistics.
     */
    @Test
    public void testStats() {
        String str;
        try {
            getClient().storeText(testFile, testText);
            getClient().retrieveText(testFile);
            str = getClient().sendAndReceive("STAT " + testFile);
            assertTrue(str.indexOf(testFile) > 0);
            str = getClient().sendAndReceive("STAT");
            assertTrue(str.indexOf("Bytes uploaded: ") > 0);
            assertTrue(str.indexOf("Bytes downloaded: ") > 0);
            assertTrue(str.indexOf("Files uploaded: ") > 0);
            assertTrue(str.indexOf("Files downloaded: ") > 0);
            getClient().sendAndReceive("DELE " + testFile);
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: change file date.
     */
    @Test
    public void testTouch() {
        String str;
        try {
            getClient().storeText(testFile, testText);
            getClient().sendAndReceive("MDTM 19800202111111 " + testFile);
            getClient().list(testFile);
            str = getClient().getTextData();
            assertTrue(str.indexOf("Feb 02 11:11") > 0);
            getClient().sendAndReceive("DELE " + testFile);
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: Store, retrieve and delete.
     */
    @Test
    public void testStoreRetrieve() {
        String str;
        try {
            getClient().storeText(testFile, testText);
            str = getClient().sendAndReceive("SIZE " + testFile).trim();
            assertTrue(str.startsWith("213"));
            String[] parts = str.split(" ");
            int fileSize = Integer.parseInt(parts[1]);

            getClient().retrieveText(testFile);
            str = getClient().getTextData();
            assertEquals(testText, str);
            getClient().list(testFile);
            str = getClient().getTextData();
            assertTrue(str.length() > 0);
            getClient().appendText(testFile, testText);
            getClient().retrieveText(testFile);
            str = getClient().getTextData();
            assertEquals(testText + testText, str);
            str = getClient().sendAndReceive("SIZE " + testFile).trim();
            assertTrue(str.startsWith("213"));
            parts = str.split(" ");
            int doublefileSize = Integer.parseInt(parts[1]);
            assertEquals(fileSize * 2, doublefileSize);
            getClient().sendAndReceive("DELE " + testFile);
            getClient().list(testFile);
            str = getClient().getTextData();
            assertTrue(str.length() == 0);
        } catch (IOException e) {
            log.error(e);
            fail(e.toString());
        }
    }

    /**
     * Test case: Block transfer, record structures.
     */
    @Test
    public void testBlockTransfer() {
        try {
            byte[] data = createBlockData();
            String str = getClient().sendAndReceive("MODE B");
            assertTrue(str.startsWith("200"));
            str = getClient().sendAndReceive("STRU R");
            assertTrue(str.startsWith("200"));
            str = getClient().sendAndReceive("TYPE E");
            assertTrue(str.startsWith("200"));
            str = getClient().storeRaw(testFile, data);
            assertTrue(str.startsWith("226"));

            getClient().sendAndReceive("MODE S");
            getClient().sendAndReceive("STRU F");
            getClient().retrieveText(testFile);
            str = getClient().getTextData();
            String br = System.getProperty("line.separator");
            assertTrue(str.startsWith("ABBB" + br + "CCCCCCCC"));

            getClient().sendAndReceive("MODE S");
            getClient().sendAndReceive("STRU R");
            getClient().retrieveRaw(testFile);
            byte[] raw = getClient().getRawData();
            assertTrue(ArrayUtils.contains(raw, (byte) 0xFF));

            getClient().sendAndReceive("MODE B");
            getClient().sendAndReceive("STRU R");
            getClient().sendAndReceive("TYPE E");
            getClient().retrieveRaw(testFile);
            raw = getClient().getRawData();
            assertTrue(Arrays.equals(ArrayUtils.subarray(raw, 0, 11), new byte[] {
                    -128, 0, 4, (byte) 0xC1, (byte) 0xC2, (byte) 0xC2, (byte) 0xC2, -64, 1, 2, (byte) 0xC3}));
            getClient().sendAndReceive("DELE " + testFile);
        } catch (IOException e) {
            log.error(e);
        }

    }

    private byte[] createBlockData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0);
        baos.write(0);
        baos.write(1);
        int len = 1;
        for (int i = 0; i < len; i++) {
            baos.write((byte) 0xC1);
        }
        baos.write(BlockModeConstants.DESC_CODE_EOR);
        baos.write(0);
        baos.write(3);
        len = 3;
        for (int i = 0; i < len; i++) {
            baos.write((byte) 0xC2);
        }
        baos.write(BlockModeConstants.DESC_CODE_REST);
        baos.write(0);
        baos.write(1);
        baos.write(5);

        baos.write(BlockModeConstants.DESC_CODE_EOR | BlockModeConstants.DESC_CODE_EOF);
        baos.write(1);
        baos.write(2);
        len = (1 << 8) + 2;
        for (int i = 0; i < len; i++) {
            baos.write((byte) 0xC3);
        }
        baos.flush();
        baos.close();

        byte[] data = baos.toByteArray();
        return data;
    }
}
// CHECKSTYLE:ON
