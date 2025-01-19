package WanFeng;



import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;

import org.spigotmc.event.entity.EntityMountEvent;//高版本: org.bukkit.event.entity.EntityMountEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class playerIntercept implements Listener {

    private final Main plugin;
    private static Set<String> blockedPlayers;

    public playerIntercept(Main plugin) {
        this.plugin = plugin;
        blockedPlayers = new HashSet<>();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        blockedPlayers.remove(player.getName());
    }
    // Intercepts container interactions
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player && blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts entity interactions
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts command usage
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName()) && !event.getMessage().startsWith("/login") && !event.getMessage().startsWith("/l")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts item switching
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts item editing
    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts sneaking actions
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("["+Main.config.server_name()+"]请使用\"/login 密码\"指令登录到服务器，若您还未注册，请前往我们的网站("+Main.config.website_reg()+")注册");
        blockedPlayers.add(player.getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts block place
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts player interactions
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    // Intercepts player movements
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setTo(event.getFrom());
        }
    }

    // Intercepts player damage events
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && blockedPlayers.contains(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }
    // Intercepts player drop item events
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
    // Intercepts player pickup item events
    @EventHandler
    public void onPlayerAttemptPickupItem(EntityPickupItemEvent event) {
        if (blockedPlayers.contains(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }

    // Intercepts player attacks
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && blockedPlayers.contains(event.getDamager().getName())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (blockedPlayers.contains(((Player) event.getWhoClicked()).getName())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (blockedPlayers.contains(player.getName())) {
            event.getInventory().setResult(null);
            player.sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if (blockedPlayers.contains(event.getEnchanter().getName())) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player && blockedPlayers.contains(((Player) event.getEntity()).getName())) {
            event.setCancelled(true);
            ((Player) event.getEntity()).sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].substring(1);
        if (command.equalsIgnoreCase("login") || command.equalsIgnoreCase("l")) {
            if (blockedPlayers.contains(player.getName())) {
                String[] args = event.getMessage().split(" ");
                login(player.getName(), args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
            }
            else{
                player.sendMessage("[" + Main.config.server_name() + "]您已登录!");
            }
        }
        else{
            if (blockedPlayers.contains(player.getName())) {
                event.setCancelled(true);
                player.sendMessage("["+Main.config.server_name()+"]请先使用\"/login 密码\"登录!");
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        if (blockedPlayers.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("["+Main.config.server_name()+"]请先登录!");
        }
    }

    //玩家登录
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

    public static void unblockPlayer(String playerName) {
        blockedPlayers.remove(playerName);
    }

    public static Set<String> BlockedPlayers() {
        return blockedPlayers;
    }

}




