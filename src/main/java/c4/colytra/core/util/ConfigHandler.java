/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Colytra.
 * Colytra is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.colytra.core.util;

import c4.colytra.Colytra;
import c4.colytra.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;

public class ConfigHandler {

    private static final String CATEGORY_GENERAL = "general";
    public static Configuration cfg;
    private static String[] blacklist = new String[] {};
    public static ArrayList<Item> blacklisted = new ArrayList<>();

    public static void readConfig() {
        try {
            cfg = CommonProxy.config;
            cfg.load();
            initConfig();
        } catch (Exception e1) {
            Colytra.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initConfig() {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        blacklist = cfg.getStringList("Blacklist", CATEGORY_GENERAL, blacklist, "A list of items that cannot be attached with an elytra");
        initBlacklist();
    }

    private static void initBlacklist() {

        if (blacklist.length > 0) {

            for (String s : blacklist) {
                Item item = Item.getByNameOrId(s);
                if (item != null) {
                    blacklisted.add(item);
                }
            }
        }
    }

    @Mod.EventBusSubscriber
    private static class ConfigChangeHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
            if (e.getModID().equals(Colytra.MODID)) {
                initConfig();

                if (cfg.hasChanged()) {
                    cfg.save();
                }
            }
        }
    }
}