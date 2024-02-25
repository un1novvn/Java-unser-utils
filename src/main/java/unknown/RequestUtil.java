package unknown;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class RequestUtil {
    public static void get(String url) throws Exception {
        get(url,null);
    }

    public static void get(String url, HashMap<String,String> params) throws Exception {

        String query = "?";
        if(params != null){
            for (String key : params.keySet()) {
                String value = params.get(key);
                query += key +"="+value;
                query += "&";
            }
        }
        query = query.substring(0,query.length()-1);
        url += query;
        // 创建 URL 对象
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // 获取响应
        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response Headers: " + con.getHeaderFields());

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }catch (Exception e){
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }finally {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("Response: " + response.toString());
        }
    }
    public static void post(String url,HashMap<String,String> params) throws Exception{

        // 创建 URL 对象
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        String body = "";
        for (String key : params.keySet()) {
            String value = params.get(key);
            body += key +"="+value;
            body += "&";
        }
        body = body.length() == 0 ? "" : body.substring(0,body.length()-1);

        // 设置请求方法为 POST
        con.setRequestMethod("POST");
        // 设置请求头部信息
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 发送 POST 请求
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(body);
            wr.flush();
        }

        // 获取响应
        int responseCode = con.getResponseCode();
        System.out.println("Response Headers: " + con.getHeaderFields().size());
        System.out.println("Response Code: " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // 输出响应内容
            System.out.println("Response Text: " + response);
        }
    }
}
