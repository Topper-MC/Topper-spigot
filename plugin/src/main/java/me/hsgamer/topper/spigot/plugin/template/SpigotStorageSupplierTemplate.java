package me.hsgamer.topper.spigot.plugin.template;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.converter.NumberFlatValueConverter;
import me.hsgamer.topper.storage.flat.converter.UUIDFlatValueConverter;
import me.hsgamer.topper.storage.flat.core.FlatValueConverter;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.storage.sql.converter.NumberSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.UUIDSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;
import me.hsgamer.topper.template.storagesupplier.StorageSupplierTemplate;
import me.hsgamer.topper.template.storagesupplier.storage.DataStorageSupplier;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class SpigotStorageSupplierTemplate implements StorageSupplierTemplate {
    private final SpigotDataStorageSupplierSettings dataStorageSupplierSettings;
    private final Map<String, Function<Settings, DataStorageSupplier>> supplierMap;
    private final Function<Settings, DataStorageSupplier> defaultSupplier;

    public SpigotStorageSupplierTemplate(TopperPlugin plugin) {
        this.dataStorageSupplierSettings = new SpigotDataStorageSupplierSettings(plugin);
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

    @Override
    public DataStorageSupplier getDataStorageSupplier(Settings settings) {
        return supplierMap.getOrDefault(settings.storageType().toLowerCase(Locale.ROOT), defaultSupplier).apply(settings);
    }

    public Function<String, DataStorage<UUID, Double>> getNumberStorageSupplier() {
        return getDataStorageSupplier(dataStorageSupplierSettings).getStorageSupplier(
                new UUIDFlatValueConverter(),
                new NumberFlatValueConverter<>(Number::doubleValue),
                new UUIDSqlValueConverter("uuid"),
                new NumberSqlValueConverter<>("value", true, Number::doubleValue)
        );
    }
}
