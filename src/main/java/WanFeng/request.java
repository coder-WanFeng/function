package WanFeng;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class request{


    public static String sendRequest(String route, String method, String body) {
        HttpURLConnection connection = null;
        try {
            // 创建URL对象
            URL url = new URL(Main.config.py_server_url()+route);
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法
            connection.setRequestMethod(method);

            // 如果是POST请求，需要设置以下属性
            if ("POST".equals(method)) {
                connection.setDoOutput(true);
                // 发送POST请求必须设置如下两行
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
                connection.setUseCaches(false);

                // 发送请求参数
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(body);
                wr.close();
            }

            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 返回响应内容
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
