package WanFeng;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collection;

import static org.bukkit.Bukkit.getServer;

public class cloudMessages implements Listener {

    public static void startUpdateCloudMessage(Plugin ourVillageTools) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateCloudMessages();
            }
        }.runTaskTimer(ourVillageTools, 0L, 20L); // 0L是延迟时间（ticks），10L是间隔时间（ticks）
    }

    private static void updateCloudMessages() {
        String response = request.sendRequest("server-messages/?post_from=website", "GET", "");
        if (response != null && !response.isEmpty()) {
            String delimiter = "OurVillageTools云消息分割!!!(顺便打广告)[腐竹/管理:羡小冰.Fellow、烟火气、千纥、挽枫·随黄昏而去]";
            String[] MessagesList = response.split(delimiter);
            Collection<? extends Player> onlinePlayers = getServer().getOnlinePlayers();
            for (String message : MessagesList){
                for (Player player : onlinePlayers) {
                    player.sendMessage("["+Main.config.server_name()+"]"+message);
                }
            }
        }
    }

    @EventHandler
    public void postCloudMessage(AsyncPlayerChatEvent event){
        String player_name=event.getPlayer().getName();
        if (!playerIntercept.BlockedPlayers().contains(player_name)) {
            String message=event.getMessage();
            String request_body="{\"player_name\":\""+escapeJsonString(player_name)+"\",\"cloud_message\":\""+escapeJsonString(message)+"\",\"post_from\":\"server\"}";
            request.sendRequest("/server-messages/","POST",request_body);
        }
    }

    private String escapeJsonString(String string) {
        return string.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}


