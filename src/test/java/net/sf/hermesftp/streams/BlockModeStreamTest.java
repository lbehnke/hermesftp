/*
 ------------------------------
 Hermes FTP Server
 Copyright (c) 2006 Lars Behnke
 ------------------------------

 This file is part of Hermes FTP Server.

 Hermes FTP Server is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sf.hermesftp.streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

// CHECKSTYLE:OFF

/**
 * Testcase for block mode streams.
 * 
 * @author Lars Behnke
 */
public class BlockModeStreamTest {

    /**
     * Testcase : input stream.
     */
    @Test
    public void testBlockInputStream() {
        try {
            Map<Long, Long> markers = new HashMap<Long, Long>();
            byte[] data = createBlockData();
            byte[] eor = new byte[] { 0x25 };
            BlockModeInputStream bmis = new BlockModeInputStream(
                    new ByteArrayInputStream(data), eor, markers);
            ByteArrayOutputStream destData = new ByteArrayOutputStream();
            int b;
            while ((b = bmis.read()) >= 0) {
                destData.write(b);
            }
            byte[] destBytes = destData.toByteArray();
            assertNotNull(markers.get(new Long(5)));
            assertEquals(264, destBytes.length);
            assertEquals(1, markers.size());
            assertEquals(2, destBytes[0]);
            assertEquals(4, destBytes[1]);
            assertEquals(4, destBytes[2]);
            assertEquals(4, destBytes[3]);
            assertEquals(0x25, destBytes[4]);
            assertEquals(6, destBytes[5]);
            assertEquals(6, destBytes[262]);
            assertEquals(0x25, destBytes[263]);
        } catch (IOException e) {
            fail(e.toString());
        }
    }

    /**
     * Testcase : input stream.
     */
    @Test
    public void testBlockOutputStream() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BlockModeOutputStream bmos = new BlockModeOutputStream(baos, 16);
            byte[] data = createStreamData();

            int lastByte = -1;
            for (int i = 0; i < data.length; i++) {
                byte b = data[i];
                if (b != lastByte && lastByte != -1) {
                    bmos.finalizeRecord(false);
                }
                bmos.write(b);
                lastByte = b;

            }
            bmos.close();
            baos.close();

            byte[] destBytes = baos.toByteArray();
            assertEquals(319, destBytes.length);
            assertEquals(BlockModeConstants.DESC_CODE_EOR, destBytes[0] & 0xff);
            assertEquals(0, destBytes[1]);
            assertEquals(1, destBytes[2]);
            assertEquals(2, destBytes[3]);
            assertEquals(BlockModeConstants.DESC_CODE_EOR, destBytes[4] & 0xff);
            assertEquals(0, destBytes[5]);
            assertEquals(3, destBytes[6]);
            assertEquals(4, destBytes[7]);
            assertEquals(4, destBytes[8]);
            assertEquals(4, destBytes[9]);

            assertEquals(0, destBytes[10]);
            assertEquals(0, destBytes[11]);
            assertEquals(16, destBytes[12]);
            assertEquals(6, destBytes[13]);

            assertEquals(BlockModeConstants.DESC_CODE_EOF
                    | BlockModeConstants.DESC_CODE_EOR, destBytes[314] & 0xff);
            assertEquals(0, destBytes[315]);
            assertEquals(2, destBytes[316]);
            assertEquals(6, destBytes[317]);
            assertEquals(6, destBytes[318]);

        } catch (IOException e) {
            fail(e.toString());
        }
    }

    private byte[] createStreamData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(2);
        int len = 3;
        for (int i = 0; i < len; i++) {
            baos.write(4);
        }
        len = (1 << 8) + 2;
        for (int i = 0; i < len; i++) {
            baos.write(6);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    private byte[] createBlockData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0);
        baos.write(0);
        baos.write(1);
        int len = 1;
        for (int i = 0; i < len; i++) {
            baos.write(2);
        }
        baos.write(BlockModeConstants.DESC_CODE_EOR);
        baos.write(0);
        baos.write(3);
        len = 3;
        for (int i = 0; i < len; i++) {
            baos.write(4);
        }
        baos.write(BlockModeConstants.DESC_CODE_REST);
        baos.write(0);
        baos.write(1);
        baos.write(5);

        baos.write(BlockModeConstants.DESC_CODE_EOR
                | BlockModeConstants.DESC_CODE_EOF);
        baos.write(1);
        baos.write(2);
        len = (1 << 8) + 2;
        for (int i = 0; i < len; i++) {
            baos.write(6);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

}
// CHECKSTYLE:ON
