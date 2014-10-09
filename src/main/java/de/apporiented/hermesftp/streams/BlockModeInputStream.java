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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

/**
 * Reads a stream formatted in block mode, extracts the data contents and registers restart markers.
 * 
 * @author Lars Behnke
 */
public class BlockModeInputStream extends InputStream implements RecordReadSupport, BlockModeConstants {

    private static final String UNEXPECTED_END_OF_STREAM = "Unexpected end of stream.";

    private byte[]              buffer;

    private int                 idx;

    private long                byteCount;

    private InputStream         is;

    private Map<Long, Long>     restartMarkers           = new HashMap<Long, Long>();

    private byte[]              eorMarkerBytes;

    private boolean             eof;

    private boolean             eor;

    /**
     * Constructor.
     * 
     * @param is the input stream.
     * @param eorMarker The marker bytes for EOR.
     * @param restartMarkers Hash table to be filled with restart markers.
     */
    public BlockModeInputStream(InputStream is, byte[] eorMarker, Map<Long, Long> restartMarkers) {
        super();
        this.is = is;
        if (eorMarker == null) {
            String lineSep = System.getProperty("line.separator");
            eorMarker = lineSep.getBytes();
        }
        this.eorMarkerBytes = eorMarker;
        if (restartMarkers == null) {
            restartMarkers = new HashMap<Long, Long>();
        }
        this.restartMarkers = restartMarkers;
    }

    /**
     * Constructor.
     * 
     * @param is The input stream.
     */
    public BlockModeInputStream(InputStream is) {
        this(is, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] readRecord() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while (!(eor && idx == 0)) {
                int b = read();
                if (b == -1) {
                    throw new IOException(UNEXPECTED_END_OF_STREAM);
                }
                baos.write(b);
            }
        } finally {
            baos.close();
        }
        return baos.toByteArray();

    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException {
        if (buffer == null) {
            if (eof) {
                return -1;
            }
            int descriptor = is.read();
            int hi = is.read();
            int lo = is.read();
            checkHeader(descriptor, hi, lo);
            int len = hi << 8 | lo;
            buffer = new byte[len];
            int realLength = is.read(buffer);
            if (realLength != len) {
                throw new IOException(UNEXPECTED_END_OF_STREAM);
            }
            eof = (descriptor & BlockModeConstants.DESC_CODE_EOF) > 0;
            eor = (descriptor & BlockModeConstants.DESC_CODE_EOR) > 0;
            boolean mark = (descriptor & BlockModeConstants.DESC_CODE_REST) > 0;
            if (mark) {
                setRestartMarker();
                buffer = null;
                return read();
            }
            byteCount += len;
            if (eor) {
                buffer = ArrayUtils.addAll(buffer, eorMarkerBytes);
            }
        }
        int result = buffer[idx];
        idx++;
        if (idx >= buffer.length) {
            buffer = null;
            idx = 0;
        }
        return result;
    }

    private void checkHeader(int descriptor, int hi, int lo) throws IOException {
        if (descriptor == -1 || hi == -1 || lo == -1) {
            throw new IOException(UNEXPECTED_END_OF_STREAM);
        }
        if ((descriptor & BlockModeConstants.DESC_CODE_ERR) > 0) {
            throw new IOException("Error flag in descriptor code set.");
        }
    }

    private void setRestartMarker() throws IOException {
        if (buffer.length > 8) {
            throw new IOException("Marker size exceeds 8 bytes.");
        }
        long marker = 0;
        int len = buffer.length;
        for (int i = 0; i < len; i++) {
            marker |= buffer[i] << (8 * (len - i - 1));
        }
        restartMarkers.put(new Long(marker), new Long(byteCount));
    }

    /**
     * Getter method for the java bean <code>restartMarkers</code>.
     * 
     * @return Returns the value of the java bean <code>restartMarkers</code>.
     */
    public Map<Long, Long> getRestartMarkers() {
        return restartMarkers;
    }

}
