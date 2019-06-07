package cn.wode490390.nukkit.banrecipe;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.Lists;
import java.util.List;

public class BanRecipe extends PluginBase implements Listener {

    private static final String PERMISSIONS_BANRECIPE_CRAFT = "banrecipe.craft";

    private static final String CONFIG_MSG = "craft-msg";
    private static final String CONFIG_CRAFT = "ban.craft-result";
    private static final String CONFIG_SMELT = "ban.smelt-result";
    private static final String CONFIG_FUEL = "ban.smelt-fuel";

    private String msg;
    private List<Integer> craft;
    private List<Integer> smelt;
    private List<Integer> fuel;

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this);
        } catch (Exception ignore) {

        }
        this.saveDefaultConfig();
        Config config = this.getConfig();
        String node = CONFIG_CRAFT;
        try {
            this.craft = config.getIntegerList(node);
        } catch (Exception e) {
            this.craft = Lists.newArrayList();
            this.logLoadException(node);
        }
        node = CONFIG_SMELT;
        try {
            this.smelt = config.getIntegerList(node);
        } catch (Exception e) {
            this.smelt = Lists.newArrayList();
            this.logLoadException(node);
        }
        node = CONFIG_FUEL;
        try {
            this.fuel = config.getIntegerList(node);
        } catch (Exception e) {
            this.fuel = Lists.newArrayList();
            this.logLoadException(node);
        }
        if (!this.craft.isEmpty()) {
            node = CONFIG_MSG;
            String value = "&cThis item cannot be crafted!";
            try {
                this.msg = TextFormat.colorize(config.getString(node, value));
            } catch (Exception e) {
                this.msg = TextFormat.colorize(value);
                this.logLoadException(node);
            }
        } else if (this.smelt.isEmpty() && this.fuel.isEmpty()) {
            return;
        }
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(PERMISSIONS_BANRECIPE_CRAFT) && this.craft.contains(event.getRecipe().getResult().getId())) {
            player.sendMessage(this.msg);
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (this.smelt.contains(event.getResult().getId())) {
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (this.fuel.contains(event.getFuel().getId())) {
            event.setCancelled();
        }
    }

    private void logLoadException(String node) {
        this.getLogger().alert("An error occurred while reading the configuration '" + node + "'. Use the default value.");
    }
}
