package unknown;

import java.net.URL;

public class Tmp {



    public static void test(){

        String classFilePath = Gadget.class.getName().replace('.', '/') + ".class";
        // This gets the path relative to the package structure
        // e.g., com/example/MyClass.class

        // Get the resource URL
        String classFileUrl = Gadget.class.getClassLoader().getResource(classFilePath).getFile();


        System.out.println(classFileUrl);

    }


}
