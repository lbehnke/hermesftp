package net.sf.hermesftp.console;

import net.sf.hermesftp.exception.FtpConsoleException;

/**
 * Interface of the FTP console.
 * 
 * @author Administrator
 * 
 */
public interface ConsoleServer {

    /**
     * Starts the console.
     * 
     * @throws FtpConsoleException
     */
    public abstract void start() throws FtpConsoleException;

}