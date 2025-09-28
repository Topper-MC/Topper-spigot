package me.hsgamer.topper.spigot.plugin.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.spigot.plugin.storage.DataStorageSupplier;
import me.hsgamer.topper.spigot.plugin.storage.DataStorageSupplierSetting;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.core.FlatValueConverter;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class StorageManager {
    private final Map<String, Function<DataStorageSupplierSetting, DataStorageSupplier>> supplierMap;
    private final Function<DataStorageSupplierSetting, DataStorageSupplier> defaultSupplier;

    public StorageManager() {
        this.supplierMap = new HashMap<>();
        this.defaultSupplier = setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                return new PropertiesDataStorage<>(setting.getBaseFolder(), name, keyConverter, valueConverter);
            }
        };
        this.supplierMap.put("flat", defaultSupplier);
        this.supplierMap.put("yaml", setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                return new ConfigFileDataStorage<K, V>(setting.getBaseFolder(), name, keyConverter, valueConverter) {
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
        });
        this.supplierMap.put("json", setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                return new ConfigFileDataStorage<K, V>(setting.getBaseFolder(), name, keyConverter, valueConverter) {
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
        });
        this.supplierMap.put("sqlite", setting -> {
            SqliteDataStorageSupplier supplier = new SqliteDataStorageSupplier(setting.getBaseFolder(), setting.getDatabaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
        this.supplierMap.put("new-sqlite", setting -> {
            SqliteDataStorageSupplier supplier = new NewSqliteDataStorageSupplier(setting.getBaseFolder(), setting.getDatabaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
        this.supplierMap.put("mysql", setting -> {
            MySqlDataStorageSupplier supplier = new MySqlDataStorageSupplier(setting.getDatabaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
    }

    public DataStorageSupplier getSupplier(String type, DataStorageSupplierSetting setting) {
        return supplierMap.getOrDefault(type.toLowerCase(Locale.ROOT), defaultSupplier).apply(setting);
    }
}
