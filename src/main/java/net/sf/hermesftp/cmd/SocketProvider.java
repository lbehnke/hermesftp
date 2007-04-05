package net.sf.hermesftp.cmd;

import java.io.IOException;
import java.net.Socket;

import net.sf.hermesftp.exception.FtpConfigException;

/**
 * Interface implemented by classes that provide for the data channel in passive or active transfer
 * mode.
 * 
 * @author Behnke
 */
public interface SocketProvider {

    /**
     * Initializes the provider.
     * 
     * @throws IOException
     * @returns Information about the data channel is provided.
     */
    public DataChannelInfo init() throws IOException;

    /**
     * Provides the socket for data transfer. Multiple calls of this method do not result in
     * multiple socket instance. One instance is created and cached.
     * 
     * @return The Socket.
     * @throws FtpConfigException
     * @throws IOException
     */
    public Socket provideSocket() throws IOException;

    /**
     * Closes the socket, if necessary.
     */
    public void closeSocket();

}
