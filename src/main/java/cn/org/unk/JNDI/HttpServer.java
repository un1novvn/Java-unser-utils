package cn.org.unk.JNDI;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class HttpServer {

    private int port;

    /*
        hashMap.put("/aaa","123".getBytes());
        浏览器访问/aaa，即可响应123
     */
    private HashMap<String,byte[]> route;

    public HttpServer(int port, HashMap<String,byte[]> route){
        this.port = port;
        this.route = route;
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("HTTP Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());

            String requestLine = in.readLine();
            System.out.println("Received request: " + requestLine);

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                return;
            }

            String httpMethod = requestParts[0];
            String url = requestParts[1];

            if ("GET".equals(httpMethod)) {
                byte[] response = getResponseForUrl(url);
                sendHttpResponse(out, "HTTP/1.1 200 OK", "text/plain", response);
            } else {
                sendHttpResponse(out, "HTTP/1.1 405 Method Not Allowed", "text/plain", "Method not allowed".getBytes());
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getResponseForUrl(String url) {

        if(route.containsKey(url)){
            return route.get(url);
        }else{
            return "404 Not Found".getBytes();
        }

    }

    private static void sendHttpResponse(BufferedOutputStream out, String statusLine, String contentType, byte[] response) throws IOException {
        out.write((statusLine + "\r\n").getBytes());
        out.write(("Content-Type: " + contentType + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(response);
        out.flush();
    }
}
