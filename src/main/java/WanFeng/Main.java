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
        if(config.function_update_player_state()){getServer().getPluginManager().registerEvents(new listenPlayerState(), this);};
        if(config.function_player_protect()){getServer().getPluginManager().registerEvents(new playerIntercept(this), this);};
        if(config.function_chat_with_AI()){getServer().getPluginManager().registerEvents(new chatWithBigModel(this),this);};
        if(config.function_cloud_message()){
            getServer().getPluginManager().registerEvents(new cloudMessages(),this);
            cloudMessages.startUpdateCloudMessage(this);//开始同步云消息
        };
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
        static boolean function_player_protect(){return configs.getString("player_protect").replace("\"", "\\\"").equals("true");}
        static boolean function_chat_with_AI(){return configs.getString("chat_with_AI").replace("\"", "\\\"").equals("true");}
        static boolean function_cloud_message(){return configs.getString("cloud_message").replace("\"", "\\\"").equals("true");}
        static boolean function_update_player_state(){return configs.getString("update_player_state").replace("\"", "\\\"").equals("true");}

    }

}
