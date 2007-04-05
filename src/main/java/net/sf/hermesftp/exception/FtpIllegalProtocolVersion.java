package net.sf.hermesftp.exception;

/**
 * Generated, if preferred IP version is not supported (see EPSV command).
 * 
 * @author Behnke
 */
public class FtpIllegalProtocolVersion extends FtpException {

    private static final long serialVersionUID = 6508595945287849478L;

    /**
     * Constructor.
     */
    public FtpIllegalProtocolVersion() {
        super();
    }

    /**
     * Constructor.
     */
    public FtpIllegalProtocolVersion(String message) {
        super(message);
    }

    protected String getCategory() {
        return "IP version";
    }

}
