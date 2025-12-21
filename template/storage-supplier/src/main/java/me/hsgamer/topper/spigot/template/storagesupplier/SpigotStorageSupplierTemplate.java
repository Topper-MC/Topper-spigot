package me.hsgamer.topper.spigot.template.storagesupplier;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.SqlClient;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.core.FlatValueConverter;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;
import me.hsgamer.topper.template.storagesupplier.StorageSupplierTemplate;
import me.hsgamer.topper.template.storagesupplier.storage.DataStorageSupplier;
import me.hsgamer.topper.template.storagesupplier.storage.FlatDataStorageSupplier;
import me.hsgamer.topper.template.storagesupplier.storage.SqlDataStorageSupplier;

import java.io.File;
import java.util.Locale;

public abstract class SpigotStorageSupplierTemplate implements StorageSupplierTemplate {
    public abstract SqlClient<?> getSqlClient(Setting setting);

    @Override
    public DataStorageSupplier getDataStorageSupplier(Settings settings) {
        switch (settings.storageType().toLowerCase(Locale.ROOT)) {
            case "yaml": {
                return new FlatDataStorageSupplier() {
                    @Override
                    public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter) {
                        return new ConfigFileDataStorage<K, V>(settings.baseFolder(), name, keyConverter, valueConverter) {
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
                };
            }
            case "json": {
                return new FlatDataStorageSupplier() {
                    @Override
                    public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter) {
                        return new ConfigFileDataStorage<K, V>(settings.baseFolder(), name, keyConverter, valueConverter) {
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
                };
            }
            case "sqlite": {
                SqliteDataStorageSupplier supplier = new SqliteDataStorageSupplier(settings.baseFolder(), settings.databaseSetting(), this::getSqlClient);
                return SqlDataStorageSupplier.of(supplier);
            }
            case "new-sqlite": {
                SqliteDataStorageSupplier supplier = new NewSqliteDataStorageSupplier(settings.baseFolder(), settings.databaseSetting(), this::getSqlClient);
                return SqlDataStorageSupplier.of(supplier);
            }
            case "mysql": {
                MySqlDataStorageSupplier supplier = new MySqlDataStorageSupplier(settings.databaseSetting(), this::getSqlClient);
                return SqlDataStorageSupplier.of(supplier);
            }
            default: {
                return new FlatDataStorageSupplier() {
                    @Override
                    public <K, V> DataStorage<K, V> getStorage(String name, FlatValueConverter<K> keyConverter, FlatValueConverter<V> valueConverter) {
                        return new PropertiesDataStorage<>(settings.baseFolder(), name, keyConverter, valueConverter);
                    }
                };
            }
        }
    }
}
