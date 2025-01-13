package WanFeng;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();//保存默认配置文件
        //注册监听事件
        getServer().getPluginManager().registerEvents(new listenPlayerState(), this);
        getServer().getPluginManager().registerEvents(new cloudMessages(),this);
        getServer().getPluginManager().registerEvents(new playerIntercept(this), this);
        //初始化配置文件
        config.configs();
        //看来是同步云消息
        cloudMessages.startUpdateCloudMessage(this);
    }

    @Override
    public void onDisable() {
        listenPlayerState.onServerDisable();
    }

    public void login(String playerName, String[] args) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        String response = request.sendRequest("player-login/", "POST", "{\"player_name\":\"" + playerName + "\",\"password\":\"" + args[0] + "\"}");
        if (response != null && player != null) {
            String message = response.substring(6);
            if (response.startsWith("true ")) {
                playerIntercept.unblockPlayer(playerName);
                player.sendMessage("["+Main.config.server_name()+"]"+message);
                listenPlayerState.updateAfterLogin(playerName);
            } else {
                player.kickPlayer(message);
            }
        }
    }
    public static class config{

        private static FileConfiguration configs;
        public static void configs() {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            configs = new YamlConfiguration();
            try {
                configs.load(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        static String py_server_url() {
            String py_server_url = "http://" + configs.getString("py_server_ip") + ":" + configs.getString("py_server_port") + "/";
            return py_server_url.replace("\"", "\\\"");
        }
        static String server_name() {
            return configs.getString("server_name").replace("\"", "\\\"");
        }
        static  String website_reg(){
            String website_reg=configs.getString("website_reg");
            if(Objects.equals(website_reg, "")){
                return "暂无注册页!请联系管理员解决!";
            }
            else{
                return  website_reg;
            }
        }
    }

}
