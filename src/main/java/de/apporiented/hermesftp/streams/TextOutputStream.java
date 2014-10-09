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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Returns text data as byte arrays. In contrast to WriterOutputStream this class supports records.
 * Internally, the a Writer is used to perform the translation from characters to bytes.
 * 
 * @author Lars Behnke
 */
public class TextOutputStream extends OutputStream implements RecordWriteSupport {

    private OutputStream          os;

    private ByteArrayOutputStream byteBuffer;

    private OutputStreamWriter    writer;

    private String                inputEncoding;

    private String                outputEncoding;

    /**
     * Constructor.
     * 
     * @param os The output stream.
     * @param outputEncoding The encoding of outbound text data.
     * @throws UnsupportedEncodingException Thrown if encoding is unknown.
     */
    public TextOutputStream(OutputStream os, String outputEncoding) throws UnsupportedEncodingException {
        this(os, outputEncoding, null);
    }

    /**
     * Constructor.
     * 
     * @param os The output stream.
     * @param outputEncoding The encoding of outbound text data.
     * @param inputEncoding The encoding of inbound text data.
     * @throws UnsupportedEncodingException Thrown if encoding is unknown.
     */
    public TextOutputStream(OutputStream os, String outputEncoding, String inputEncoding)
            throws UnsupportedEncodingException {
        super();

        this.os = os;
        this.byteBuffer = new ByteArrayOutputStream();
        this.writer = new OutputStreamWriter(os, outputEncoding);
        this.inputEncoding = inputEncoding;
        this.outputEncoding = outputEncoding;
    }

    /**
     * {@inheritDoc}
     */
    public void write(int b) throws IOException {
        byteBuffer.write(b);
    }

    /**
     * {@inheritDoc}
     */
    public void writeRecord(byte[] data, boolean eof) throws IOException {
        String line;
        if (inputEncoding != null) {
            line = new String(data, inputEncoding);
        } else {
            line = new String(data);
        }

        if (os instanceof RecordWriteSupport) {
            ((RecordWriteSupport) os).writeRecord(line.getBytes(outputEncoding), eof);
        } else {
            writer.write(line + System.getProperty("line.separator"));
        }

    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws IOException {
        byte[] data = byteBuffer.toByteArray();
        String s;
        if (inputEncoding != null) {
            s = new String(data, inputEncoding);
        } else {
            s = new String(data);
        }
        writer.write(s);
        writer.flush();
        byteBuffer.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        flush();
        byteBuffer.close();
        writer.close();
    }

}
