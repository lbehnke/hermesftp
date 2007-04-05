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

    
    public DataChannelInfo(String addr, int port) {
        this(addr, port, 1);
    }
    
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
