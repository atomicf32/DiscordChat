package nullfedora.discordchat;

import nullfedora.util.DiscordWebhook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DiscordChat extends JavaPlugin implements Listener {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        config.addDefault("webhookURL", "insert-webhook-url");
        List<String> defaultBannedWords = Arrays.asList("@everyone", "@here");
        config.addDefault("banned-words", defaultBannedWords);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws IOException {
        if(event.isAsynchronous()) {
            if (config.contains("webhookURL")) {
                if (!Objects.equals(config.getString("webhookURL"), "insert-webhook-url")) {
                    DiscordWebhook webhook = new DiscordWebhook(config.getString("webhookURL"));

                    String message = event.getMessage();
                    if(config.contains("banned-words")) {
                        for(Object word : Objects.requireNonNull(config.getList("banned-words"))) {
                            message = message.replaceAll((String) word, "");
                        }
                    }
                    webhook.setContent(message);

                    webhook.setUsername(event.getPlayer().getDisplayName());
                    webhook.setAvatarUrl(String.format("https://cravatar.eu/helmhead/%s/68.png", event.getPlayer().getDisplayName()));
                    if(!message.equals("")) {
                        webhook.execute();
                    }
                }
            }
        }
    }
}
