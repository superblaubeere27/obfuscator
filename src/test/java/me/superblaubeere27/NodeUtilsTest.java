package me.superblaubeere27;

import me.superblaubeere27.jobf.utils.NameUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NodeUtilsTest {
    @Test
    public void test_getPackage() {
        assertEquals(NameUtils.getPackage("className"), "");
        assertEquals(NameUtils.getPackage("package1/className"), "package1");
        assertEquals(NameUtils.getPackage("package1/package2/className"), "package1/package2");

        try {
            NameUtils.getPackage("/className");
            fail();
        } catch (Exception ignored) {
        }
    }
}
