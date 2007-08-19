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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * In a record structured file EOR and EOF will each be indicated by a two-byte control code. The
 * first byte of the control code will be all ones, the escape character. The second byte will have
 * the low order bit on and zeros elsewhere for EOR and the second low order bit on for EOF; that
 * is, the byte will have value 1 for EOR and value 2 for EOF. EOR and EOF may be indicated together
 * on the last byte transmitted by turning both low order bits on (i.e., the value 3). If a byte of
 * all ones was intended to be sent as data, it should be repeated in the second byte of the control
 * code.
 * 
 * @author Lars Behnke
 */
public class RecordInputStream extends InputStream implements RecordReadSupport {

    private static final int ESCAPE_CODE = 0xFF;

    private InputStream      is;

    private byte[]           eorMarker;

    private int              eorMarkerIdx;

    private boolean          completed;

    /**
     * Constructor.
     * 
     * @param is The input stream.
     */
    public RecordInputStream(InputStream is) {
        this(is, null);
    }

    /**
     * Constructor.
     * 
     * @param is The input stream.
     * @param eorMarker The byte sequence the EOR marker is translated to (e.g. line break).
     */
    public RecordInputStream(InputStream is, byte[] eorMarker) {
        super();
        this.is = is;
        if (eorMarker == null) {
            String lineSep = System.getProperty("line.separator");
            eorMarker = lineSep.getBytes();
        }
        this.eorMarker = eorMarker;
        this.eorMarkerIdx = eorMarker.length;
    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException {
        int b = -1;
        if (eorMarkerIdx < eorMarker.length) {
            b = eorMarker[eorMarkerIdx++];
        } else if (completed) {
            b = -1;
        } else {
            b = is.read();
            if (b == ESCAPE_CODE) {
                b = processControlCode();
            }
        }
        return b;
    }

    /**
     * Reads a complete record, excluding the end marker.
     * 
     * @return The record without EOR/EOF marker.
     * @throws IOException If something goes wrong.
     */
    public byte[] readRecord() throws IOException {
        if (completed) {
            return null;
        }
        List<Byte> byteList = new ArrayList<Byte>();
        boolean done = false;
        while (!done) {
            int b = is.read();
            if (b == -1) {
                throw new IOException("Unexpected end of file. No EOF marker found.");
            } else if (b == ESCAPE_CODE) {
                b = is.read();

                if (b != ESCAPE_CODE) {
                    boolean eor = (b & 1) > 0;
                    boolean eof = (b & 2) > 0;
                    if (eof) {
                        completed = true;
                        done = true;
                    }
                    if (eor) {
                        done = true;
                    }
                    continue;
                }
            }
            byteList.add(new Byte((byte) b));
        }
        return createByteArrayByList(byteList);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        is.close();
        if (!completed) {
            throw new IOException("No EOF marker found.");
        }
    }

    private byte[] createByteArrayByList(List<Byte> byteList) {
        int idx = 0;
        byte[] result = new byte[byteList.size()];
        for (Byte bObj : byteList) {
            result[idx++] = bObj.byteValue();
        }
        return result;
    }

    /**
     * Processes the control code and returns the next data byte.
     * 
     * @return The next data byte.
     * @throws IOException
     */
    private int processControlCode() throws IOException {
        int b;
        b = is.read();
        if (b != ESCAPE_CODE) {
            boolean eor = (b & 1) > 0;
            boolean eof = (b & 2) > 0;
            if (eof) {
                completed = true;
            }
            if (eor) {
                eorMarkerIdx = 0;
                b = read();
            }
        }
        return b;
    }

}
