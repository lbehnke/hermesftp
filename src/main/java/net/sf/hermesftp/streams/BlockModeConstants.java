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

/**
 * Common constants used by streams that handle block modes.
 * 
 * @author Lars Behnke
 */
public interface BlockModeConstants {

    /** End of data block is EOR. */
    static final int DESC_CODE_EOR  = 128;

    /** End of data block is EOF. */
    static final int DESC_CODE_EOF  = 64;

    /** Suspected errors in data block. */
    static final int DESC_CODE_ERR  = 32;

    /** Data block is a restart marker. */
    static final int DESC_CODE_REST = 16;

}
