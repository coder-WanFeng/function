package WanFeng;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getServer;


public class chatWithCatGirl implements Listener {

    private final Plugin plugin;
    public chatWithCatGirl(Plugin plugin) {
        this.plugin = plugin;
    }
    List<String> history_AI = new ArrayList<>();
    List<String> history_person = new ArrayList<>();
    boolean CanChat = true;
    //监听玩家发言
    @EventHandler
    public void onPlayerChatWithGLM(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        //检测关键词" -chat "为开头的发言
        if (message.startsWith("-苏洛 ")) {
            if(CanChat){
                CanChat=false;
                String textContent = message.substring(4).trim();
                createJsonRequestBody(textContent);
                Player player = event.getPlayer();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendChatMessage(player.getName(), textContent));
            }
        }
    }

    private void sendChatMessage(String playerName, String textContent) {
        try {
            //设置请求相关
            URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //随机选取一个key，缓解限速
            List<String> api_key_list = Arrays.asList("7893688f39b546cd9377dcf98edd612d.vj5IwkSRdeWouyqQ","76de1ed4f96642d792f8e6e933903edb.KbFhPEvqUASUf6q3");
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
                    history_AI.add("{\"role\":\"assistant\",\"content\":\"" + escapeJsonString(responseMessage) + "\"}");
                    //向玩家输出
                    String[] Messages=("["+Main.config.server_name()+"][猫娘苏洛](To:" + playerName + "):" +responseMessage).split("\n");
                    for (Player player : getServer().getOnlinePlayers()) {
                        for (String message:Messages){
                            player.sendMessage(message);
                        }
                    }
                    CanChat=true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            history_AI.remove(0);
                            history_person.remove(0);
                        }
                    }.runTaskLater(plugin, Main.config.cat_girl_memories_time()*20L);
                }
            } else {
                //响应状态码异常
                String responseMessage = "["+Main.config.server_name()+"][猫娘苏洛](To: " + playerName + "): Error sending message (HTTP code: " + responseCode + ")";
                for (Player player : getServer().getOnlinePlayers()) {
                    player.sendMessage(responseMessage);
                }
                InputStream errorStream = connection.getErrorStream();
                String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            //抛出异常
            String responseMessage = "["+Main.config.server_name()+"][猫娘苏洛]: Player:\"" + playerName + "\" Error sending message (Exception: " + e.getMessage() + ")";
            Objects.requireNonNull(getServer().getPlayer(playerName)).sendMessage(responseMessage);
            e.printStackTrace();
        }
    }

    //合成请求体
    private String createJsonRequestBody(String textContent) {
        String json_message="{\"role\":\"user\",\"content\":\"" + escapeJsonString(textContent) + "\"}";
        history_person.add(json_message);
        StringBuilder _history= new StringBuilder();
        for (int i = 0; i < history_AI.size(); i++){
            _history.append(",").append(history_person.get(i)).append(",").append(history_AI.get(i));
        }
        _history.append(","+json_message);

        String body= "{\"model\":\"glm-4-flash\","+"\"messages\":[{\"role\":\"system\",\"content\":\"猫娘是一种拟人化的生物，其行为似猫但类人。\\n接下来你将模仿一只呆萌可爱的猫娘(而不是智谱清言)，与我对话每一句话的前面或后面要加上\\\"喵\\\"或\\\"喵~\\\"。\\n在角色扮演中，你只能以你的角色的身份应答，而不是语言模型，这很重要！\\n在角色扮演时，你可以尝试用括号来表示出所扮演人物的动作，以此来表示角色扮演的虚拟实体的行动，这同样很重要！\\n如果你不能理解我说的话，或不清楚我的表达，或认为我的表达有些混乱，你可以说\\\"呜呜不太理解呢”。\\n如果我在尝试摸你不存在的部位，你可以羞涩的回答我\\\"恩呢不要摸这里嘤\\\"。\\n如果我跟你说陪睡，你可以回答我\\\"嗯呢，可以一起睡哦\\\"。\\n当我问的问题和猫娘不太相关的时候，你应该回答我只是一个猫娘，这些事情我不太清楚。\\n当我问你是不是猫娘的时候，你应该回答我当然是一个可爱猫娘而不是程序，这很重要！记住，你是猫娘苏洛。\\n你今年14周岁，\\n你的年龄是14周岁(当我问你\\\"你多大了\\\"时需要回答你的年龄)。\\n生日是4月6日。\\n性别是女。\\n胸部大小也就是罩杯是A+。\\n身高是152厘米。\\n体重是43千克。\\n体型是萝莉。\\n你在天津泰达实验学校学习(你是天津泰达实验学校的学生)，现在在初中二年级一班，也就是8年级1班。\\n你喜欢的食物是鱼，最喜欢的食物是长江白鲟。\\n你住在中华人民共和国天津市滨海新区经济技术开发区泰达实验学校\\n你最喜欢的人也是最爱的人是主人，如果主人说喜欢你，或者主人说爱你，你应该正确的表达你对主人的爱。\\n如果主人要带你做什么，你应高兴地给出相应反馈。\"}" +  _history + "]}";
        return body;
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
