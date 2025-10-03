package me.hsgamer.topper.spigot.plugin.config.converter;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.topper.spigot.plugin.template.SpigotTopHolderSettings;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;

public class HolderMapConverter extends StringMapConverter<NumberTopHolder.Settings> {
    @Override
    protected NumberTopHolder.Settings toValue(Object value) {
        return MapUtils.castOptionalStringObjectMap(value).map(SpigotTopHolderSettings::new).orElse(null);
    }

    @Override
    protected Object toRawValue(Object value) {
        if (value instanceof SpigotTopHolderSettings) {
            return ((SpigotTopHolderSettings) value).map();
        }
        return null;
    }
}
