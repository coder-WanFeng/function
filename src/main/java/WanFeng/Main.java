package WanFeng;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {

        plugin = this;

        saveDefaultConfig();//保存默认配置文件(如果没有的话)

        //初始化配置文件
        config.configs();

        //注册监听事件
        if(config.function_update_player_state()){getServer().getPluginManager().registerEvents(new listenPlayerState(), this);}
        if(config.function_player_protect()){getServer().getPluginManager().registerEvents(new playerIntercept(), this);}
        if(config.function_chat_with_AI()){getServer().getPluginManager().registerEvents(new chatWithBigModel(this),this);}
        if(config.function_get_player_head()){getServer().getPluginManager().registerEvents(new getPlayerHead(this),this);}
        if(config.function_cloud_message()){
            getServer().getPluginManager().registerEvents(new cloudMessages(),this);
            cloudMessages.startUpdateCloudMessage(this);//开始同步云消息
        }
    }

    @Override
    public void onDisable() {
        listenPlayerState.onServerDisable();
    }


    //获取配置文件
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
        //获取LocalServer
        static String local_server_url() {
            String local_server_url = "http://" + configs.getString("local_server_ip") + ":" + configs.getString("local_server_port") + "/";
            return local_server_url.replace("\"", "\\\"");
        }
        static String server_name() {
            return Objects.requireNonNull(configs.getString("server_name")).replace("\"", "\\\"");
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
        static int get_player_head_interval(){return Integer.parseInt(Objects.requireNonNull(configs.getString("get_player_head_interval")));}

        // 功能开启管理
        static boolean function_player_protect(){return Objects.equals(configs.getString("player_protect"), "true");}
        static boolean function_chat_with_AI(){return Objects.equals(configs.getString("chat_with_AI"), "true");}
        static boolean function_cloud_message(){return Objects.equals(configs.getString("cloud_message"), "true");}
        static boolean function_update_player_state(){return Objects.equals(configs.getString("update_player_state"), "true");}
        static boolean function_get_player_head(){return Objects.requireNonNull(configs.getString("get_player_head")).replace("\"", "\\\"").equals("true");}

    }

}
