package cn.org.unk.agent;

import cn.org.unk.Util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class AgentLoader {

    private String JVMName;
    public AgentLoader(String JVMName){
        this.JVMName = JVMName;
    }

    /**
     这个方法，作者为了自己方便，将本机常用的agent的位置硬编码在包里，这样就不用每次都写路径。
     */
    public void loadPOJONodeAgent() throws Exception {
        // Use default agent
        loadAgent("E:\\ideaProjects\\main_agent_jackson\\out\\artifacts\\main_agent_jackson_jar\\main_agent_jackson.jar");
    }
    // 提供绝对路径
    public void loadAgent(String path) throws Exception {

        File toolsPath = new File(System.getProperty("java.home").replace("jre","lib") + File.separator + "tools.jar");
        URL url = toolsPath.toURI().toURL();
        URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{url});
        Class<?> MyVirtualMachine = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");

        List list = (List)Util.invokeStaticMethod(MyVirtualMachine, "list", null, null);

        for(Object vmd : list){

            String displayName = (String)Util.invokeMethod(vmd, "displayName", null, null);
            String id = (String)Util.invokeMethod(vmd, "id", null, null);

            //JVM名称就是main方法所在的类名
            if(displayName.equals(JVMName)){
                System.out.println("Attach to JVM: "+JVMName);
                Object virtualMachine = Util.invokeStaticMethod(MyVirtualMachine, "attach", new Class[]{String.class}, new Object[]{id});

//                String path = ClassLoader.getSystemClassLoader().getResource("Agent-POJONode.jar").getPath();

                if(path.charAt(0) == '/' && System.getProperty("os.name").toLowerCase().contains("win")){
                    path = path.substring(1);
                }

                System.out.println("Start to load agent: " + path);
                Util.invokeMethod(virtualMachine,"loadAgent",new Class[]{String.class}, new Object[]{path});
                //断开JVM连接
                System.out.println("Detach from JVM: "+JVMName);
                Util.invokeMethod(virtualMachine,"detach",null,null);
            }
        }
    }

}
