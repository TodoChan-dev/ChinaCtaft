package jp.tproject.chinaCtaft;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class ChinaCtaft extends JavaPlugin implements Listener {

    private static final List<String> CRAZY_NAMES = List.of(
            "木之镐すごい斧です〜树枝ツル",
            "나무 ピッケル ح木하نツ",
            "Деревяный 镐頭 из 木",
            "ツ木な나무⚒ кирка",
            "木制镐ح木ما",
            "镐ツ⚒木ыйデス〜",
            "木ツح나ムピッケル"
    );

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            player.getWorld().createExplosion(player.getLocation(), 5F, true, true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        // ランダムな名前を取得
        Random random = new Random();
        String randomName = CRAZY_NAMES.get(random.nextInt(CRAZY_NAMES.size()));

        // クラフティングインベントリを取得
        CraftingInventory inventory = event.getInventory();

        // 結果アイテムを取得
        ItemStack result = inventory.getResult();
        if (result == null) return; // 結果が空の場合は終了

        // アイテムMetaを取得して変更
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            Component displayName = Component.text(randomName);
            meta.displayName(displayName);
            result.setItemMeta(meta);

            // 結果をインベントリに反映
            inventory.setResult(result);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block targetBlock = player.getTargetBlock(null, 15);

        // 緑色ブロックを見た時のテレポート
        if (isGreenBlock(targetBlock.getType())) {
            Location randomLoc = getRandomLocation(player.getLocation(), 50);
            player.teleport(randomLoc);
        }

        // 上を見た時の奈落落とし
        if (player.getLocation().getPitch() < -60) { // 上を見上げている状態
            Location loc = player.getLocation();
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    for (int y = 0; y >= -200; y--) {  // 200ブロック分空気に
                        loc.clone().add(x, y, z).getBlock().setType(Material.AIR);
                    }
                }
            }

            // 効果音とパーティクルを追加してより派手に
            player.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();

        // 5体の村人を召喚
        for (int i = 0; i < 5; i++) {
            Villager villager = (Villager) playerLoc.getWorld().spawnEntity(playerLoc, EntityType.VILLAGER);

            // 村人がプレイヤーを追いかける処理
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        villager.remove();
                        this.cancel();
                        return;
                    }

                    villager.setTarget(player);
                }
            }.runTaskTimer(this, 0L, 20L);
        }
    }

    private boolean isGreenBlock(Material material) {
        return material.name().contains("LEAVES") ||
                material.name().contains("VINE") ||       // ツタ
                material == Material.GRASS_BLOCK ||
                material == Material.LIME_CONCRETE ||
                material == Material.GREEN_WOOL ||
                material == Material.GREEN_CONCRETE ||
                material == Material.GREEN_TERRACOTTA ||
                material == Material.GREEN_CONCRETE_POWDER ||
                material == Material.GREEN_STAINED_GLASS ||
                material == Material.GREEN_STAINED_GLASS_PANE ||
                material == Material.GREEN_GLAZED_TERRACOTTA ||
                material == Material.GREEN_SHULKER_BOX ||
                material == Material.GREEN_CARPET ||
                material == Material.GREEN_BANNER ||
                material == Material.MOSS_BLOCK ||        // 苔ブロック
                material == Material.MOSS_CARPET ||       // 苔カーペット
                material == Material.SUGAR_CANE ||        // サトウキビ
                material == Material.CACTUS ||            // サボテン
                material == Material.SEAGRASS ||          // 海草
                material == Material.KELP ||              // ケルプ
                material == Material.BAMBOO ||            // 竹
                material == Material.SHORT_GRASS ||             // 草
                material == Material.TALL_GRASS ||        // 背の高い草
                material == Material.FERN ||              // シダ
                material == Material.LARGE_FERN ||        // 大きいシダ
                material.name().contains("AZALEA") ||     // ツツジ関連
                material == Material.SLIME_BLOCK;         // スライムブロック
    }

    private Location getRandomLocation(Location center, int radius) {
        Random random = new Random();
        int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
        int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;
        int y = center.getWorld().getHighestBlockYAt(x, z);

        return new Location(center.getWorld(), x, y, z);
    }

}
