

public class WinCalc {
    static {
        try {
            Runtime.getRuntime().exec("calc");
        } catch (Exception e) {
            System.out.println("error!");
        }
    }
}
