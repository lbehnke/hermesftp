Hermes FTP Server
-----------------

  Hermes FTP Server is a free, cross-platform FTP server adhering to RFC 959, RFC 2228, RFC 2389, and RFC 2428.
  It is being developed in Java with a strong focus on security and extensibility. 
  Currently, these features are supported:

    * Active and passive transfer mode.
    
    * Customizable data channel ports (firewall friendly).
    
    * Block transmission mode and mainframe record structure.
    
    * ASCII, EBCDIC, and UTF-8 codepages.
    
    * Data encryption via 128-Bit Secure Sockets Layer (SSL/TLS).
    
    * Authentication via implicit or explicit SSL/TLS.
    
    * Network protocols IPv4 and IPv6.
    
    * Data compression (MODE Z).
    
    * Executable as Windows service or Linux daemon.

    * Management of users and user groups via XML file.
    
    * Supports anonymous FTP users.
    
    * Global and group based bandwidth limits.
    
    * Group based resource consumption limits
    
    * Group based data access control.
    
    * User password encryption.
    
    * Per-user and per-session statistics.
    
    * Embedded web console for monitoring statistics, logs etc. via browser.
    
    * Configurable log4j logging.
    
    * Strong focus on extensibility and abilitity to integrate into different environments.
      
   Hermes FTP server is a console application, configured exclusively through XML files. 
   An embedded web server allows for
   monitoring resource consumption, session statistics, user statistics and log files via web browser.

   Apart from FTP specific settings, such as connection limits or supported cipher suites, the internal
   structure of the application can be configured as well. The IoC based architecture allows for registering
   new FTP commands or replacing existing ones without the need to recompile the Hermes FTP server application.
   Hence, by reimplementing a subset of the FTP command classes, you could make Hermes FTP server behave like
   an EJB client delegating requests directly to an application server instead of accessing the local file
   system. Hermes FTP Server is based on the Spring Framework. So, if you are familiar with this framework and
   its underlying concept of dependency injection  you will get started quickly developing customized
   extensions.

Maven2 Settings
---------------
Add these server configuration to the Maven 2 settings.conf:
    
    <server>
      <id>sourceforge-site</id>
      <username>your-sourceforge-user-name</username>
      <password>your-sourceforge-password</password>
    </server>
    
    <server>
      <id>sourceforge-repo</id>
      <username>your-sourceforge-user-name</username>
      <password>your-sourceforge-password</password>
    </server>


Building the Application
------------------------
Please make sure the port 2121 is not bound already, since test server and client
make use of this port.
# mvn install

Assembling the application
-------------------------
# mvn assembly:assembly

Creating the project site
-------------------------
# mvn site

Deploying the project site
--------------------------
# mvn site-deploy

Creating a bundle for deployment on remote repository
-----------------------------------------------------
# mvn repository:bundle-create
# mvn deploy

Releasing the application
-------------------------
# mvn release:clean
# mvn --batch-mode release:prepare
# mvn --batch-mode release:perform




