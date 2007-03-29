package net.sf.hermesftp.utils;

import java.util.Properties;

import junit.framework.TestCase;

public class VarMergerTest extends TestCase {

    public void testMerge() {
        VarMerger vm = new VarMerger("ABC ${abc} DEF ${def} GHI ${ghi}");
        Properties props = new Properties();
        props.put("abc", "ABC");
        props.put("def", "\\");
        props.put("ghi", "$");
        vm.merge(props);
        String result = vm.getText();
        assertEquals("ABC ABC DEF \\ GHI $", result);
    }
}
