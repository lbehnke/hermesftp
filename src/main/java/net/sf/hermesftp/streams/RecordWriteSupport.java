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

/**
 * Implemented by streams that support writing records.
 * 
 * @author Lars Behnke
 */
public interface RecordWriteSupport {

    /**
     * Writes out a whole record.
     * 
     * @param data The record data.
     * @param eof True, if last record is passed.
     * @throws IOException Thrown if writing fails.
     */
    void writeRecord(byte[] data, boolean eof) throws IOException;

    /**
     * Flushes any buffered record data.
     * 
     * @throws IOException Thrown if IO fails.
     */
    void flush() throws IOException;
}
