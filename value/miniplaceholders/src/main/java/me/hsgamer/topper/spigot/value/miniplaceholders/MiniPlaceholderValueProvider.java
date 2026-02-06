package me.hsgamer.topper.spigot.value.miniplaceholders;

import io.github.miniplaceholders.api.MiniPlaceholders;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class MiniPlaceholderValueProvider implements ValueProvider<Player, String> {
    private final String placeholder;

    public MiniPlaceholderValueProvider(String placeholder) {
        this.placeholder = normalizePlaceholder(placeholder);
    }

    private static String normalizePlaceholder(String placeholder) {
        return placeholder.isEmpty() || placeholder.startsWith("<") || placeholder.endsWith(">")
                ? placeholder
                : "<" + placeholder + ">";
    }

    @Override
    public void accept(Player player, Consumer<ValueWrapper<String>> callback) {
        String parsed;
        try {
            TagResolver tagResolver = MiniPlaceholders.audienceGlobalPlaceholders();
            Component component = MiniMessage.miniMessage().deserialize(placeholder, player, tagResolver);
            parsed = PlainTextComponentSerializer.plainText().serialize(component).trim();
        } catch (Exception e) {
            callback.accept(ValueWrapper.error("Error while parsing the placeholder: " + placeholder, e));
            return;
        }
        callback.accept(placeholder.equals(parsed) ? ValueWrapper.notHandled() : ValueWrapper.handled(parsed));
    }
}
