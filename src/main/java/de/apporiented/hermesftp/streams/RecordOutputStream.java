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

import java.io.IOException;
import java.io.OutputStream;

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
public class RecordOutputStream extends OutputStream implements RecordWriteSupport {

    private static final int ESCAPE_CODE = 0xFF;

    private OutputStream     os;

    private boolean          fileCompleted;

    private boolean          recordCompleted;

    /**
     * Constructor.
     * 
     * @param os The output stream.
     */
    public RecordOutputStream(OutputStream os) {
        super();
        this.os = os;
    }

    /**
     * Writes a whole record and sets the end marker.
     * 
     * @param record The record to transmit.
     * @param eof End of file is reached.
     * @throws IOException Thrown if somethings goes wrong.
     */
    public void writeRecord(byte[] record, boolean eof) throws IOException {
        recordCompleted = false;
        write(record);
        finalizeRecord(eof);
    }

    /**
     * {@inheritDoc}
     */
    public void write(int b) throws IOException {
        checkCompleted();
        if ((b & ESCAPE_CODE) == ESCAPE_CODE) {
            os.write(ESCAPE_CODE);
        }
        os.write(b);
        recordCompleted = false;
    }

    /**
     * A flush ends the current record. {@inheritDoc}
     */
    public void flush() throws IOException {
        os.flush();
    }

    /**
     * Flushes the buffer and sets an EOR / EOF marker.
     * 
     * @param eof True if end of file.
     * @throws IOException thrown if writing to stream fails.
     */
    public void finalizeRecord(boolean eof) throws IOException {
        if (!fileCompleted && !recordCompleted) {
            byte code = 0;
            code |= 1;
            code |= eof ? 2 : 0;
            os.write(ESCAPE_CODE);
            os.write(code);
            if (eof) {
                fileCompleted = true;
            }
            recordCompleted = true;
        }
    }

    /**
     * Before the stream is closed and EOF marker is set. {@inheritDoc}
     */
    public void close() throws IOException {
        finalizeRecord(true);
        os.close();
    }

    private void checkCompleted() throws IOException {
        if (fileCompleted) {
            throw new IOException("EOF marker already set.");
        }
    }

}
