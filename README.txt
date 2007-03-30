Hermes FTP Server
-----------------

Hermes FTP Server is a free, cross-platform FTP server adhering to RFC 775, RFC 959, RFC 2228,
and RFC 2389. It is fully implemented in Java, documented, and was developed
with extensibility in mind. Currently, the following features are supported:

 - Active and passive transfer mode.</li>
 - Authentication via implicit or explicit SSL/TLS.</li>
 - Data protection via SSL/TLS.</li>
 - ASCII and EBCDIC codepages.</li>
 - User and user group manager.
 - Group based resource consumption limits and data access permissions.</li>
 - Usage statistics.</li>


Hermes FTP comes as console application, configured exclusively through XML files.
In its current version there is no interactive administration client available.

Apart from FTP specific settings, such as connection limits or supported cipher suites, the internal
structure of the application can be configured as well. You can implement new commands or
modify existing commands without the need to recompile the whole application. If you are familiar with
the Spring Framework you are probably also familiar with the concept of "dependency injection", also known as
"inversion of control". Hermes FTP is based on the Spring Framework and hence adopts this concept making it
easy to extend the functionality of the software.

Building the Application
------------------------

Please make sure the Ports 2121 is not bound already, since test server and client
make use of this port.

mvn source:jar install

Assembling the application
-------------------------

mvn assembly:assembly

Creating the project site
-------------------------

mvn site

Deploying the project site
--------------------------

mvn site-deploy

Creating a bundle for deployment on remote repository
-----------------------------------------------------
mvn source:jar javadoc:jar repository:bundle-create
mvn source:jar deploy

