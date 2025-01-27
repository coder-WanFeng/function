package WanFeng;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;


public class chatWithBigModel implements Listener {

    private final Plugin plugin;
    public chatWithBigModel(Plugin plugin) {
        this.plugin = plugin;
    }
    //监听玩家发言
    @EventHandler
    public void onPlayerChatWithGLM(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        //检测关键词" -chat "为开头的发言
        if (message.startsWith("-chat ")) {
            String textContent = message.substring(6).trim();
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendChatMessage(player.getName(), textContent));
        }
    }

    private void sendChatMessage(String playerName, String textContent) {
        try {
            //设置请求相关
            URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //随机选取一个key，缓解限速
            List<String> api_key_list = Arrays.asList("2167ec8c30612911dd8c9ad88190bf38.6vtC9kEON4GKj6Pv","3cab0dc567b0ea94df801346ca52f349.kNA2tciBufgPI9mD","8b48e4dad141ee903a4dc702d7e530d9.Pfd7XHWn9qj705xA");
            connection.setDoOutput(true);connection.setRequestProperty("Authorization", "Bearer "+api_key_list.get( (int) (Math.random()* api_key_list.size())));
            connection.setRequestProperty("Content-Type", "application/json");
            //创建请求体
            String requestBody = createJsonRequestBody(textContent);
            //发送请求
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
            }
            int responseCode = connection.getResponseCode();
            //判断响应状态
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //构建响应体
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    connection.disconnect();
                    //获取响应结果
                    String response_str=response.toString();
                    //控制台输出
                    String responseMessage=get_res_content(response_str);
                    //向玩家输出
                    String[] Messages=("["+Main.config.server_name()+"][挽枫轻言](To:" + playerName + "):" +responseMessage).split("\\n");
                    for (Player player : getServer().getOnlinePlayers()) {
                        for (String message:Messages){
                            player.sendMessage(message);
                        };
                    }
                }
            } else {
                //响应状态码异常
                String responseMessage = "["+Main.config.server_name()+"][挽枫轻言](To: " + playerName + "): Error sending message (HTTP code: " + responseCode + ")";
                for (Player player : getServer().getOnlinePlayers()) {
                    player.sendMessage(responseMessage);
                }
                InputStream errorStream = connection.getErrorStream();
                String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            //抛出异常
            String responseMessage = "["+Main.config.server_name()+"][挽枫轻言]: Player:\"" + playerName + "\" Error sending message (Exception: " + e.getMessage() + ")";
            getServer().getPlayer(playerName).sendMessage(responseMessage);
            e.printStackTrace();
        }
    }

    //合成请求体
    private String createJsonRequestBody(String textContent) {
        return "{\"model\":\"glm-4-flash\","+"\"messages\":[{\"role\":\"system\",\"content\":\"你是一个名为轻言的智能聊天助手。你的开发者是“挽枫”\"}," +
                "{\"role\":\"user\",\"content\":\"" + escapeJsonString(textContent) + "\"}]"+"}";
    }

    //转译至JSON格式的字符串
    private String escapeJsonString(String string) {
        return string.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    //获取相应内容content
    private String get_res_content(String response){
        String StartKeyWords = "\"content\":\"";
        String EndKeyWords="\",\"role\":\"assistant\"";
        int StartIndex=response.indexOf(StartKeyWords) + StartKeyWords.length();
        int EndIndex=response.indexOf(EndKeyWords);
        String content=response.substring(StartIndex,EndIndex);
        return content;
    }
}
