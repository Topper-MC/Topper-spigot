package me.hsgamer.topper.spigot.plugin.storage;

import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;

import java.io.File;

public interface DataStorageSupplierSetting {
    SqlDatabaseSetting getDatabaseSetting();

    File getBaseFolder();
}
