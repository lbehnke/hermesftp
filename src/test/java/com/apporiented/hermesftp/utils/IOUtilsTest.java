package com.apporiented.hermesftp.utils;

import com.apporiented.hermesftp.streams.RafOutputStream;
import junit.framework.TestCase;

import java.io.File;
import java.io.OutputStream;

public class IOUtilsTest extends TestCase {

    public void testCloseGracefully() throws Exception {
        File file = File.createTempFile("IOUtilsTest", ".txt");
        OutputStream os = new RafOutputStream(file);
        os.write(0);
        IOUtils.closeGracefully(os);
        try {
            os.write(1);
            fail("Closed stream expected.");
        } catch (Exception e) {

        }
        file.delete();
    }
}