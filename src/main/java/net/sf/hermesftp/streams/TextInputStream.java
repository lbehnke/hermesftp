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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Reads text data and makes the lines accessible as records. The records are returned as byte
 * arrays. Internally, the a Reader is used to perform the
 * translation from characters to bytes.
 *
 * @author Lars Behnke
 *
 */
public class TextInputStream
    extends InputStream
    implements RecordReadSupport {

    private static final int BUFFER_SIZE = 2048;

    private BufferedReader reader;

    private String outputEncoding;

    private ByteArrayInputStream byteBuffer;

    /**
     * Constructor.
     *
     * @param is The input stream.
     * @param inputEncoding The encoding.
     * @throws UnsupportedEncodingException Thrown if encoding is unknown.
     */
    public TextInputStream(InputStream is, String inputEncoding)
            throws UnsupportedEncodingException {
        this(is, inputEncoding, null);
    }

    /**
     * Constructor.
     *
     * @param is The input stream.
     * @param inputEncoding The encoding of the inbound text data.
     * @param outputEncoding The encoding of the outbound text data.
     * @throws UnsupportedEncodingException Thrown if encoding is unknown.
     */
    public TextInputStream(InputStream is, String inputEncoding, String outputEncoding)
            throws UnsupportedEncodingException {
        super();
        this.reader = new BufferedReader(new InputStreamReader(is, inputEncoding));
        this.outputEncoding = outputEncoding;
    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException {
        int result;
        if (byteBuffer == null) {
            result = -1;
        } else {
            result = byteBuffer.read();
        }
        if (result == -1) {
            char[] chars = new char[BUFFER_SIZE];
            int charCount = reader.read(chars);
            if (charCount == -1) {
                return -1;
            }
            String s = new String(chars, 0, charCount);
            if (outputEncoding == null) {
                byteBuffer = new ByteArrayInputStream(s.getBytes());
            } else {
                byteBuffer = new ByteArrayInputStream(s.getBytes(outputEncoding));
            }
            result = read();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] readRecord() throws IOException {
        byte[] result;
        String line = reader.readLine();
        if (line == null) {
            result = null;
        } else {
            if (outputEncoding == null) {
                result = line.getBytes();
            } else {
                result = line.getBytes(outputEncoding);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        super.close();
        reader.close();
    }
}
