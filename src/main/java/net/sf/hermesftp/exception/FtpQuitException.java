package net.sf.hermesftp.exception;


/**
 * Thrown if session is supposed to be ended.
 *
 * @author Lars Behnke
 */
public class FtpQuitException
    extends FtpCmdException {

    private static final long serialVersionUID = 6482130037210864886L;

    /**
     * Constructor.
     */
    public FtpQuitException() {
        super();
    }

}
