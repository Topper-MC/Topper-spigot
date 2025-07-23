package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.topper.storage.bundle.DataStorageBuilder;
import me.hsgamer.topper.storage.bundle.DataStorageSupplier;
import me.hsgamer.topper.storage.bundle.ValueConverter;
import me.hsgamer.topper.storage.core.DataStorage;

import java.io.File;

public class SpigotDataStorageBuilder {
    public static DataStorageBuilder register(DataStorageBuilder builder) {
        builder.register(setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, ValueConverter<K, V> valueConverter) {
                return new SpigotConfigDataStorage<K, V>(setting.getBaseFolder(), name, valueConverter.getKeyFlatValueConverter(), valueConverter.getValueFlatValueConverter()) {
                    @Override
                    protected Config getConfig(File file) {
                        return new BukkitConfig(file);
                    }

                    @Override
                    protected String getConfigName(String name) {
                        return name + ".yml";
                    }
                };
            }
        }, "config", "yaml", "yml");
        builder.register(setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, ValueConverter<K, V> valueConverter) {
                return new SpigotConfigDataStorage<K, V>(setting.getBaseFolder(), name, valueConverter.getKeyFlatValueConverter(), valueConverter.getValueFlatValueConverter()) {
                    @Override
                    protected Config getConfig(File file) {
                        return new GsonConfig(file);
                    }

                    @Override
                    protected String getConfigName(String name) {
                        return name + ".json";
                    }
                };
            }
        }, "json");
        return builder;
    }
}
