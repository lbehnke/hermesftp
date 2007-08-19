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

package net.sf.hermesftp.cmd;

/**
 * Wraps the connection parameter of the data channel into a single object.
 * 
 * @author Behnke
 */
public class DataChannelInfo {

    private String address;

    private int    port;

    private int    protocolIdx;

    /**
     * Constructor.
     * 
     * @param addr The IP address.
     * @param port The port.
     */
    public DataChannelInfo(String addr, int port) {
        this(addr, port, 1);
    }

    /**
     * Constructor.
     * 
     * @param addr The IP address.
     * @param port The port.
     * @param protocolIdx The protocol index (IPv4, IPv6)
     */
    public DataChannelInfo(String addr, int port, int protocolIdx) {
        super();
        this.address = addr;
        this.port = port;
        this.protocolIdx = protocolIdx;
    }

    /**
     * Getter method for <code>protocolIdx</code>.
     * 
     * @return The property <code>protocolIdx</code>.
     */
    public int getProtocolIdx() {
        return protocolIdx;
    }

    /**
     * Setter method for <code>protocolIdx</code>.
     * 
     * @param protocolIdx the protocolIdx to set
     */
    public void setProtocolIdx(int protocolIdx) {
        this.protocolIdx = protocolIdx;
    }

    /**
     * Getter methode for <code>address</code>.
     * 
     * @return The property <code>address</code>.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter methode for <code>address</code>.
     * 
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Getter methode for <code>port</code>.
     * 
     * @return The property <code>port</code>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter methode for <code>port</code>.
     * 
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

}
