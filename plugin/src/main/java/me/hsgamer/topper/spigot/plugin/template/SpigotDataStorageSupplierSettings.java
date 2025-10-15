package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.storage.sql.config.SqlDatabaseConfig;
import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;
import me.hsgamer.topper.template.storagesupplier.StorageSupplierTemplate;

import java.io.File;

public class SpigotDataStorageSupplierSettings implements StorageSupplierTemplate.Settings {
    private final TopperPlugin plugin;

    public SpigotDataStorageSupplierSettings(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String storageType() {
        return plugin.get(MainConfig.class).getStorageType();
    }

    @Override
    public SqlDatabaseSetting databaseSetting() {
        return new SqlDatabaseConfig("topper", new BukkitConfig(plugin, "database.yml"));
    }

    @Override
    public File baseFolder() {
        return new File(plugin.getDataFolder(), "top");
    }
}
