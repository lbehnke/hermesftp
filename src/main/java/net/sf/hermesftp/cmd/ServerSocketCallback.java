package net.sf.hermesftp.cmd;

import java.net.ServerSocket;

public interface ServerSocketCallback {

    ServerSocket createServerSocket();

}
