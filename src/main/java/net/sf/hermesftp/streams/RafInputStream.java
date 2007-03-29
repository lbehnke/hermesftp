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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Wrapper class for reading a RandomAccessFile through the InputStream abstraction.
 *
 * @author Lars Behnke
 *
 */
public class RafInputStream
    extends InputStream
    implements RecordReadSupport {

    private File file;

    private RandomAccessFile raf;

    private long offset;

    /**
     * Constructor.
     *
     * @param file The input file.
     */
    public RafInputStream(File file) {
        super();
        this.file = file;
    }

    /**
     * Constructor.
     *
     * @param file The input file.
     * @param offset The number of bytes to be skipped when reading the file.
     */
    public RafInputStream(File file, long offset) {
        super();
        this.file = file;
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    public int read() throws IOException {
        return getRaf().read();
    }

    /**
     * {@inheritDoc}
     */
    public int read(byte[] b) throws IOException {
        return getRaf().read(b);
    }

    /**
     * {@inheritDoc}
     */
    public int read(byte[] b, int off, int len) throws IOException {
        return getRaf().read(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        getRaf().close();
    }

    private RandomAccessFile getRaf() throws IOException {
        if (raf == null) {
            raf = new RandomAccessFile(file, "r");
            if (offset > 0) {
                raf.seek(offset);
            }
        }
        return raf;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] readRecord() throws IOException {
        String line = getRaf().readLine();
        if (line != null) {
            return line.getBytes();
        } else {
            return null;
        }
    }

}
