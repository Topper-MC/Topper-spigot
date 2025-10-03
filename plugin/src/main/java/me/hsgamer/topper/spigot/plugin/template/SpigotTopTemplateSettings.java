package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.template.topplayernumber.TopPlayerNumberTemplate;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.template.topplayernumber.storage.DataStorageSupplier;

import java.util.Map;

public class SpigotTopTemplateSettings implements TopPlayerNumberTemplate.Settings {
    private final TopperPlugin plugin;
    private final SpigotDataStorageSupplierSettings dataStorageSupplierSettings;

    public SpigotTopTemplateSettings(TopperPlugin plugin) {
        this.plugin = plugin;
        this.dataStorageSupplierSettings = new SpigotDataStorageSupplierSettings(plugin);
    }

    @Override
    public String storageType() {
        return plugin.get(MainConfig.class).getStorageType();
    }

    @Override
    public DataStorageSupplier.Settings storageSettings() {
        return dataStorageSupplierSettings;
    }

    @Override
    public Map<String, NumberTopHolder.Settings> holders() {
        return plugin.get(MainConfig.class).getHolders();
    }

    @Override
    public int taskSaveDelay() {
        return plugin.get(MainConfig.class).getTaskSaveDelay();
    }

    @Override
    public int taskSaveEntryPerTick() {
        return plugin.get(MainConfig.class).getTaskSaveEntryPerTick();
    }

    @Override
    public int taskUpdateEntryPerTick() {
        return plugin.get(MainConfig.class).getTaskUpdateEntryPerTick();
    }

    @Override
    public int taskUpdateDelay() {
        return plugin.get(MainConfig.class).getTaskUpdateDelay();
    }

    @Override
    public int taskUpdateSetDelay() {
        return plugin.get(MainConfig.class).getTaskUpdateSetDelay();
    }

    @Override
    public int taskUpdateMaxSkips() {
        return plugin.get(MainConfig.class).getTaskUpdateMaxSkips();
    }
}
