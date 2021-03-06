Introduction
============

Overview
--------
Hermes FTP Server is a free, cross-platform FTP server adhering to RFC 959, RFC 2228, RFC 2389, and RFC 2428. It is being developed in Java with a focus on security and extensibility. Currently, these features are supported:

* Active and passive transfer mode.
* Customizable data channel ports (firewall friendly).
* Block transmission mode and mainframe record structure.
* ASCII, EBCDIC, and UTF-8 code pages.
* Data encryption via 128-Bit Secure Sockets Layer (SSL/TLS).
* Authentication via implicit or explicit SSL/TLS.
* Network protocols IPv4 and IPv6.
* Data compression (MODE Z).
* Pre-configured Windows service or Linux daemon.
* Management of users and user groups via XML file.
* Global and group based bandwidth limits.
* Group based transfer volume limits
* Group based authorization model.
* User password encryption.
* IP black lists.
* Embedded web console for monitoring statistics, logs etc. via browser.
* Per-user and per-session statistics.
* Strong focus on extensibility and ability to integrate into different environments.
* Hermes FTP server is a console application, configured exclusively through XML files. An embedded web console allows for monitoring resource consumption, session statistics, user statistics and log files.

Motivation
----------
The development of yet another FTP server was driven by the motivation to provide a flexible, customizable software that is able to integrate into various environments such as J2EE.

Apart from FTP specific settings, such as connection limits or supported cipher suites, the internal structure of the application can be configured as well. The IoC based architecture allows for registering new FTP commands or replacing existing ones without the need to recompile the Hermes FTP server application. Hence, by re-implementing a subset of the FTP command classes, you could make Hermes FTP server behave like an EJB client delegating requests directly to an application server instead of accessing the local file system.

Hermes FTP Server is based on the Spring Framework. So, if you are familiar with this framework and its underlying concept of dependency injection you will get started quickly developing customized extensions.

Prerequisites
-------------
Hermes FTP Server requires Java 6 or later. Please check the java version installed on your system by typing the following command at the command prompt:

    java -version

In case you have no java (or an outdated version) installed, you can get the latest version here . There are no further requirements for running the FTP server.
Installation

Unzip the file hermesftp-<version>.zip into a target folder of your choice. Change into this folder and open the file hermesftp-ctx.properties with a text editor.
Find the entry "ftp.root.dir" and change the corresponding value into your ftp data folder (e.g. C:/ftproot). If this entry is omitted,
Hermes FTP Server will create a data folder hermesftp in the user's home directory.

In case you want to enable SSL, you have to set up a keystore first. For your convenience the create_keystore script may be used for this purpose.

All other settings can remain as they are. The options bean configuration should look like following snippet.

    # root directory defaults to ~/hermesftp
    ftp.root.dir=C:/ftproot

    # Max parallel connections
    max.connections=20

    # Timeout for session expiration
    max.idle.seconds=600

    # Internal buffer size for data transfer
    buffer.size=2048

    # Define a port range if you experience firewall issues
    allowed.passive.ports=

    # FTP command channel port
    ftp.port=21

    # Set true to force SSL
    ssl.force=false

    # Enable explicit SSL
    ssl.allow.explicit=false

    # Enable implicit SSL
    ssl.allow.implicit=false

    # FTP command channel port (SSL)
    ssl.port.implicit=990

    # Location of the keystore file (for SSL)
    ssl.keystore.file=keystore

    # Keystore password
    ssl.keystore.password=secret

    # Supported cipher suites
    ssl.cipher.suites=

    # Character set of EPCDIC machines (z/OS)
    charset.ebcdic=CP1047

    # Character set for PCs
    charset.ascii=ISO-8859-1

    # Enable web console (http://localhost:9988)
    console.enabled=true

    # Define black list IP filter expressions. Example: !127.0.0.1,!192.*.*.*
    ipv4.black.list=

Running the server
------------------
Make sure you are still in the installation directory and issue the following command at the command prompt.

    java -jar hermesftp-<version>.jar

How to avoid firewall issues
----------------------------
If you are running the ftp server behind a firewall, ftp clients will possibly experience problems transferring files, even though you opened the firewall for connections on port 21 (ftp) or 990 (ftp over ssl) respectively. This is because file data is transferred through a separate channel. The port of this data channel is defined by the ftp client (active transfer mode) or the ftp server (passive transfer mode). Most of the firewall issues can be solved by making the clients use the passive transfer mode.

Hermes FTP Server supports both transfer modes. Moreover, you are free to define the port list available for passive data transfer in the application context hermesftp-ctx.properties.

    allowed.passive.ports=9000-9200,12000-12500

If you omit this property, Hermes FTP Server lets the underlying OS decide on which data port to use.


Installing a Service
--------------------
You may want to start the FTP server as soon as the system is up an running.
Therefore, the binary distribution of Hermes FTP server was bundled with a precustomized Java Service Wrapper for Windows.

Before you install the service/daemon please check the settings in the application context file hermesftp-ctx.xml . In particular, find the option "ftp.root.dir" and set the value to an appropriate FTP data folder (e.g. c:/ftpdata or /home/ftp/data) .

Running Hermes FTP as NT Service
--------------------------------
In order to install Hermes FTP Server as a NT service you have to execute the following batch file:

    %HERMES_HOME%/hermesftp-svc.exe install

If everything went well, Hermes FTP Server is started automatically on system start from now on.
Start the server manually via this command:

    %HERMES_HOME%/hermesftp-svc.exe start

The service is uninstalled by executing the script file. Make sure the service is stopped before you try to uninstall it:

    %HERMES_HOME%/hermesftp-svc.exe uninstall

For more details about the service wrapper, please visit https://github.com/kohsuke/winsw.

Web Console
-----------
Hermes FTP Server comes with an embedded web server that allows for monitoring resource consumption, session statistics, user statistics and log files via web browser.

If you want to change the port (which is by default 9988) find the bean "console" in the application context hermesftp-ctx.xml and change the port property into a convenient value.

    console.port=80

In case you let the default settings untouched the console is accessible from your local machine through this URL:

    http://localhost:9988

The web console is protected against unauthorized access. Only users holding the administration role have permission to access the console. The administration role is granted in hermesftp-users.xml by setting the optional user attribute adminrole to true (default is false).

By default, only the user "admin" (password "admin") may access the console

     <user uid="admin" fullname="Administrator" 
         adminrole="true" password="{MD5}dEWco8+FqB35Dalf9ueiBw==">
       <group-ref name="users"/>
       <group-ref name="administrators"/>
     </user>

You can disable the web console by setting the property console.enabled (in hermesftp-ctx.properties) to false.

    console.enabled=false

User Management
---------------
The default user manager is configured via the file hermesftp-users.xml. This XML file contains the information about all ftp user groups, their resource consumption limits and permissions granted on data folders. It contains also a list of all users known to the system.

**Group Configuration**

Users of the same group share similar permissions and resource consumption limits.

Each resource consumption limit has a name and a corresponding value (see code snippet). Currently, there are four limits supported:

* Bytes downloaded: Number of bytes the user is allowed to download per day.
* Bytes uploaded: Number of bytes the user is allowed to upload per day.
* Files downloaded: Number of files the user is allowed to download per day.
* Files uploaded: Number of files the user is allowed to upload per day.
* Download rate: Maximum data transfer rate in KB/s of download stream (-1 unlimited).
* Upload rate: Maximum data transfer rate in KB/s of upload stream (-1 unlimited).

Each group contains a list of privileges granted on ftp data folders. You may use ANT style file patterns to specify the folder(s) that underdo the security constraints. More information about these patterns is available here. There are two placeholders that may be used in path names: ${ftproot} points to the ftp root folder. It corresponds to the option ftp.root.dir in the application context file hermesftp-ctx.xml. The expression ${user} is replaced by the user name. A permission level is assigned to each of the path patterns:

r: Read only access.
rw: Read/writeaccess.

The following code snippet demonstrates a typical group configuration:

	   <groups>
	       <group name="users" >
	           <limits>
	               <limit name="Bytes downloaded" value="1000000"/>
	               <limit name="Bytes uploaded" value="1000000"/>
	               <limit name="Files downloaded" value="100"/>
	               <limit name="Files uploaded" value="100"/>
	               <limit name="Download rate" value="-1"/>
	               <limit name="Upload rate" value="-1"/>
	           </limits>
	           <permissions>
	               <permission flag="rw" path="${ftproot}/${user}/**"/>
	               <permission flag="rw" path="${ftproot}/${user}"/>
	               <permission flag="r" path="${ftproot}"/>
	           </permissions>
	       </group>
	       ...
	   </groups>

For sake of security you should configure at least two groups: Users (with restricted data access permissions) and administrators (with full access permissions).

**User Configuration**

Only users that are known to the application are granted access to data folders. A user is configured along with his/her login name, fullname, and password. Each of the users may be a member of several groups.

The user's password can be configured in plain text or as hashcode calculated by an supported hash algorithm (e.g. MD5). The name of the algorithm is prepended to a BASE64 encoded string, e.g. "{MD5}Cwz8B/yoHJVquRgdhXb0qA==". If the password does not start with a curly brace, it is handled as plain text. Since Hermes FTP servers comes without GUI, the password hash is generated from the command line as follows:

    java -jar hermesftp-<version>.jar -password <secretpassword> <algorithm>

Example:

    c:\develop>java -jar hermesftp-0.2.jar -password sec MD5
    Hash: {MD5}dEWco8+FqB35Dalf9ueiBw==
    c:\develop>

The following code snippet demonstrates a typical user configuration:

    <users default-dir="${ftproot}/${user}">
       <user uid="user" 
               fullname="Test User" 
               password="user">
           <group-ref name="users"/>
       </user>
       <user uid="admin"
               fullname="Administrator"
               adminrole="true"
               password="{MD5}dEWco8+FqB35Dalf9ueiBw==">
           <group-ref name="users"/>
           <group-ref name="administrators"/>
       </user>
    </users>

You may omit the password to enable anonymous login. Anonymous users must provide an email address as password.


Security
--------
**Keystore Usage**

keytool is a utility shipped with your Java SDK. It enables you to manage keys and certificates used for self-authentication or data integrity and authentication services. The security related data is stored in a so-called keystore, which is basically a file in your local file system. Besides your own private/public key pairs also public keys of your communication partners reside in this file. If you like to learn more about the keytool or keystore, please visit this site.

Hermes FTP Server makes use of the keystore implementation provided by Sun Microsystems for negociating secure connections with FTP clients. If you wish to enable SSL security and send your own self-signed certificate to the client, change into the Hermes FTP installation folder and issue the following command from the command line.

On unix systems:

    chmod 755 *.sh
    ./create_keystore.sh

On windows systems:

    create_keystore.bat

The utility asks for some personal data, which is included with the certificate. Finally, a file called *keystore* is created in the Hermes FTP home directory. Note that keystore and key share the same password "secret". The certificate is valid for 365 days.

If you would like to have more control over the keystore and key parameters you can create the keystore manually:

    keytool -genkey -alias hermes -keyalg DSA -keystore keystore -validity 365
    -storepass secret -keypass secret

Of course, you can choose whatever parameters you like. However, keystore password and key password have to be equal.

Now that the keystore is set up, you may need to modify the configuration file hermes-ctx.xml . Find the following lines and change filename and password:

    ... <prop key="ssl.keystore.file">keystore</prop>
        <prop key="ssl.keystore.password">secret</prop> ...

Please note that the created self-signed certificate is inappropriate in a production environment. If security is essential, you should apply to a certificate authority for a properly signed server certificate, and then import it into the keystore.

FTPS Client Configuration
-------------------------
An FTP client software that supports FTP over SSL/TLS (FTPS or FTPES) is required to establish a secure connection to HermesFTP server. Don't mix FTPS up with SFTP. The latter one requires SSH and is currently not supported by HermesFTP.

Here is a short guide how to set up a secure connection using the Filezilla FTP client. Let's start with setting up an implicit FTPS connection. Implicit FTPS is an older, but still widely implemented, style in which the client connects to a different control port, and an SSL handshake is performed before any FTP commands are sent. The entire FTPS session is encrypted. Implicit FTPS does not allow for negotiation and the client should immediately challenge the FTPS Server with the TLS/SSL handshake. The Internet Assigned Numbers Authority (IANA) officially designates port 990 as the FTPS control channel port and port 989 as the FTPS data channel port. You can change the default port by setting the option ssl.port.implicit .

Open the server manager and create a new connection. Choose server name and ftps port the server listens to (e.g. 990 or 9900). The server type should be FTPS. Enter user name and password and click connect.

![https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen1.png](https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen1.png "FTPS client configuration")

When connecting the first the client asks you to accept the server certificate.

![https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen2.png](https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen2.png "Certificate")


The second way to establish a secure connection is explicit FTPS (aka FTPES, aka AUTH TLS). This is the preferred method according to RFC 4217. The client connects using plain text commands on port 21 and may negotiate a secure TLS connection during the FTP setup or at any time thereafter by sending the AUTH command.

Again, open the server manager and create a new connection. Choose server name and ftp port the server listens to (e.g. 21 or 2121). The server type should be FTPES. Enter user name and password and click connect.

![https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen3.png](https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen3.png)

After connecting successfully the ftp client console should show this sequence of commands:

![https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen4.png](https://raw.github.com/lbehnke/hermesftp/master/etc/images/screen4.png)

Extending the Hermes FTP Server
-------------------------------
Its IoC based architecture allows for extending or customizing the Hermes FTP Server easily. Moreover, if you need to embed the ftp server in your own application you certainly can do so. The following paragraphs describe both of the scenarios.

**Customizing the FTP Server**

In this example we extend the ftp server by an SITE command returning server specific information to the caller.
Open the application context hermesftp-ctx.xml and find the parser bean. Now add the highlighted line to the list of properties:

	<bean id="parser" scope="singleton"
	           class="de.apporiented.hermesftp.parser.impl.FtpCmdParserImpl">
		<property name="commands">
			<map>
			...
			<entry key="SITE" value="cmdSite" />
			...
			</map>
		</property>
	</bean>

Add a new bean to the application context and name it cmdSite.

 <bean id="cmdSite" class="example.FtpCmdSite" singleton="false" />

Now open your IDE (if not already open) and extend the class de.apporiented.hermesftp.cmd.AbstractFtpCmd as outlined in the snippet below.

    package example;
    
    import de.apporiented.hermesftp.cmd.AbstractFtpCmd;
    import de.apporiented.hermesftp.exception.FtpCmdException;

    public class FtpCmdSite
       extends AbstractFtpCmd {

       /**
       * {@inheritDoc}
       */
       public void execute() throws FtpCmdException {

           // handle command

           if (ok) {
             msgOut(MSG200_NOTED);
           } else {
             msgOut(MSG500;
           }

       }

       /**
       * {@inheritDoc}
       */
       public String getHelp() {
           return "My custom site command.";
       }

       /**
       * {@inheritDoc}
       */
       public boolean isAuthenticationRequired() {
           return true;
       }


After wrapping the compiled class into a JAR file you must copy the archive into the folder $HERMES_HOME/plugins. All classes found in this folder are loaded automatically after starting up the application. In the same way all parts of Hermes FTP Server represented by a Spring bean can be replaced.

**Embedding the FTP Server**

The snippet below demonstrates how to fire up Hermes FTP Server from within your own application.

    private void startServer(String beanRes) throws FtpException {
    
       ApplicationContext appContext = 
         new FileSystemXmlApplicationContextEx (new String[] {beanRes});

       FtpServer svr = (FtpServer) appContext.getBean(
           BeanConstants.BEAN_SERVER);
       Thread svrThread = new Thread(svr);
       svrThread.start();
    }

Remote Debugging
----------------

Stop any running instances.

    $ java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n -jar hermesftp-<version>.jar

Connect to port 4000 from your Debugger.
