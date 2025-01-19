package WanFeng;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Bukkit.getPlayer;

public class getPlayerHead implements Listener {

    private final Plugin plugin;
    public getPlayerHead(Plugin plugin) {
        this.plugin = plugin;
    }
    private static final Set<String> lockPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerCommandGetPlayerHead(PlayerCommandPreprocessEvent event) {
        Player commend_player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].substring(1);
        if (command.equalsIgnoreCase("get_player_head")) {
            if(commend_player.isOp()) {
                if(event.getMessage().split(" ").length>3 || event.getMessage().split(" ").length<2){
                    commend_player.sendMessage("["+Main.config.server_name()+"]用法错误，请使用:\"/get_player_head 玩家名 数量(可选)\"");
                }
                else{
                    Player target_player=getPlayer(event.getMessage().split(" ")[1]);
                    int head_num=event.getMessage().split(" ").length==3?Integer.parseInt(event.getMessage().split(" ")[2]):1;
                    if(head_num>64 || head_num<1){
                        commend_player.sendMessage("["+Main.config.server_name()+"]数量错误，应在1~64之间");
                    }
                    else{
                        GivePlayerHead(commend_player,target_player,head_num);
                    }
                }
            }
            else{
                if(event.getMessage().split(" ").length!=2){
                    commend_player.sendMessage("["+Main.config.server_name()+"]用法错误，请使用:\"/get_player_head 玩家名\"");
                }
                else{
                    Player target_player=getPlayer(event.getMessage().split(" ")[1]);

                    String commend_player_name=commend_player.getName();
                    if(LockPlayers().contains(commend_player_name)){
                        commend_player.sendMessage("["+Main.config.server_name()+"]头颅获取正在冷却中，本次冷却时间(非剩余时间)为"+Main.config.get_player_head_interval()+"秒");
                    }
                    else{
                        lockPlayers.add(commend_player_name);
                        commend_player.sendMessage("["+Main.config.server_name()+"]头颅获取已进入冷却，"+Main.config.get_player_head_interval()+"秒内将无法再获取玩家头颅");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                UnlockPlayerGetHead(commend_player_name);
                            }
                        }.runTaskLater(plugin, Main.config.get_player_head_interval()*20L);
                        GivePlayerHead(commend_player,target_player,1);
                    }

                }
            }
        }
    }

    public void GivePlayerHead(Player commend_player,Player target_player,int head_num){

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, head_num);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta != null) {
            // 设置头颅的拥有者，这样头颅就会显示该玩家的皮肤
            skullMeta.setOwningPlayer(target_player);
            playerHead.setItemMeta(skullMeta);
            // 判断是否成立
            if(skullMeta.getOwningPlayer()!=target_player){
                commend_player.sendMessage("["+Main.config.server_name()+"]玩家无效");
            }
            else{
                // 将头颅物品给予玩家
                commend_player.getInventory().addItem(playerHead);
            }
        }
        else{
            commend_player.sendMessage("["+Main.config.server_name()+"]玩家无效");
        }
    }

    public static void UnlockPlayerGetHead(String playerName) {
        lockPlayers.remove(playerName);
    }
    public static Set<String> LockPlayers() {
        return lockPlayers;
    }
}
