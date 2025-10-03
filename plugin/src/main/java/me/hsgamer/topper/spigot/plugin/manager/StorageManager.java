package me.hsgamer.topper.spigot.plugin.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.core.FlatValueConverter;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;
import me.hsgamer.topper.template.topplayernumber.storage.DataStorageSupplier;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class StorageManager {
    private final Map<String, Function<DataStorageSupplier.Settings, DataStorageSupplier>> supplierMap;
    private final Function<DataStorageSupplier.Settings, DataStorageSupplier> defaultSupplier;

    public StorageManager() {
        this.supplierMap = new HashMap<>();
        this.defaultSupplier = setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                return new PropertiesDataStorage<>(setting.baseFolder(), name, keyConverter, valueConverter);
            }
        };
        this.supplierMap.put("flat", defaultSupplier);
        this.supplierMap.put("yaml", setting -> new DataStorageSupplier() {
            @Override
            public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                return new ConfigFileDataStorage<K, V>(setting.baseFolder(), name, keyConverter, valueConverter) {
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
                return new ConfigFileDataStorage<K, V>(setting.baseFolder(), name, keyConverter, valueConverter) {
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
            SqliteDataStorageSupplier supplier = new SqliteDataStorageSupplier(setting.baseFolder(), setting.databaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
        this.supplierMap.put("new-sqlite", setting -> {
            SqliteDataStorageSupplier supplier = new NewSqliteDataStorageSupplier(setting.baseFolder(), setting.databaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
        this.supplierMap.put("mysql", setting -> {
            MySqlDataStorageSupplier supplier = new MySqlDataStorageSupplier(setting.databaseSetting(), JavaSqlClient::new);
            return new DataStorageSupplier() {
                @Override
                public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter, SqlValueConverter<K> sqlKeyConverter, SqlValueConverter<V> sqlValueConverter) {
                    return supplier.getStorage(name, sqlKeyConverter, sqlValueConverter);
                }
            };
        });
    }

    public DataStorageSupplier getSupplier(String type, DataStorageSupplier.Settings setting) {
        return supplierMap.getOrDefault(type.toLowerCase(Locale.ROOT), defaultSupplier).apply(setting);
    }
}
