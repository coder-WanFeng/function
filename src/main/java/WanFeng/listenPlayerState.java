package WanFeng;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class listenPlayerState implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String player_name=player.getName();
        request.sendRequest("players-join/", "POST", "{\"player_name\":\"" + player_name + "\",\"server\":\""+Main.config.server_name()+"\"}");
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String player_name=player.getName();
        request.sendRequest("players-left/", "POST", "{\"player_name\":\"" + player_name + "\",\"server\":\""+Main.config.server_name()+"\"}");
    }

    public static void updateAfterLogin(String player_name){
        request.sendRequest("players-login/", "POST", "{\"player_name\":\"" + player_name + "\",\"server\":\""+Main.config.server_name()+"\"}");
    }

    public String getOnlinePlayers(){
        return request.sendRequest("players-online/", "GET","");
    }

    public static void onServerDisable(){
        request.sendRequest("players-clear/", "POST","{\"server\":\""+Main.config.server_name()+"\"}");
    }
}


