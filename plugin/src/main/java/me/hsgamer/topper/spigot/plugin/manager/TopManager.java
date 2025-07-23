package me.hsgamer.topper.spigot.plugin.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MainConfig;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import me.hsgamer.topper.storage.bundle.DataStorageBuilder;
import me.hsgamer.topper.storage.bundle.DataStorageSetting;
import me.hsgamer.topper.storage.bundle.DataStorageSupplier;
import me.hsgamer.topper.storage.bundle.ValueConverter;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.converter.NumberFlatValueConverter;
import me.hsgamer.topper.storage.flat.converter.UUIDFlatValueConverter;
import me.hsgamer.topper.storage.sql.config.SqlDatabaseConfig;
import me.hsgamer.topper.storage.sql.converter.NumberSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.UUIDSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;

import java.io.File;
import java.util.*;

public class TopManager implements Loadable {
    private final Map<String, NumberTopHolder> topHolders = new HashMap<>();
    private final TopperPlugin instance;
    private DataStorageSupplier storageSupplier;

    public TopManager(TopperPlugin instance) {
        this.instance = instance;
    }

    @Override
    public void enable() {
        storageSupplier = instance.get(DataStorageBuilder.class).buildSupplier(
                instance.get(MainConfig.class).getStorageType(),
                new DataStorageSetting() {
                    @Override
                    public SqlDatabaseSetting getDatabaseSetting() {
                        return new SqlDatabaseConfig("topper", new BukkitConfig(instance, "database.yml"));
                    }

                    @Override
                    public File getBaseFolder() {
                        return new File(instance.getDataFolder(), "top");
                    }
                }
        );
        instance.get(MainConfig.class).getHolders().forEach((key, value) -> {
            NumberTopHolder topHolder = new NumberTopHolder(instance, key, value);
            topHolder.register();
            topHolders.put(key, topHolder);
        });
    }

    @Override
    public void disable() {
        topHolders.values().forEach(NumberTopHolder::unregister);
        topHolders.clear();
    }

    public DataStorageSupplier getStorageSupplier() {
        return storageSupplier;
    }

    public DataStorage<UUID, Double> buildStorage(String name) {
        return storageSupplier.getStorage(
                name,
                ValueConverter.of(
                        new UUIDFlatValueConverter(),
                        new NumberFlatValueConverter<>(Number::doubleValue),
                        new UUIDSqlValueConverter("uuid"),
                        new NumberSqlValueConverter<>("value", true, Number::doubleValue)
                )
        );
    }

    public Optional<NumberTopHolder> getTopHolder(String name) {
        return Optional.ofNullable(topHolders.get(name));
    }

    public List<String> getTopHolderNames() {
        return Collections.unmodifiableList(new ArrayList<>(topHolders.keySet()));
    }

    public void create(UUID uuid) {
        topHolders.values().forEach(holder -> holder.getOrCreateEntry(uuid));
    }
}
