package com.seailz.discordjar.model.webhook;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.core.Compilerable;
import com.seailz.discordjar.model.embed.Embed;
import com.seailz.discordjar.model.user.User;
import com.seailz.discordjar.utils.Snowflake;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.DiscordResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.logging.Logger;

public record IncomingWebhook(
        String name,
        int type,
        Snowflake channelId,
        String token,
        String avatar,
        Snowflake guildId,
        Snowflake id,
        Snowflake applicationId,
        User user,
        DiscordJar discordJar
) implements Compilerable {

    @Override
    public JSONObject compile() {
        return new JSONObject()
                .put("name", name)
                .put("type", type)
                .put("channel_id", channelId.id())
                .put("token", token)
                .put("avatar", avatar)
                .put("guild_id", guildId.id())
                .put("id", id.id())
                .put("application_id", applicationId.id())
                .put("user", user.compile());
    }

    public static IncomingWebhook decompile(JSONObject json, DiscordJar discordJar) {
        return new IncomingWebhook(
                json.has("name") ? json.getString("name") : null,
                json.has("type") ? json.getInt("type") : 0,
                json.has("channel_id") ? () -> json.getString("channel_id") : null,
                json.has("token") ? json.getString("token") : null,
                json.has("avatar") ? json.get("avatar").toString() : null,
                json.has("guild_id") ? () -> json.getString("guild_id") : null,
                json.has("id") ? () -> json.getString("id") : null,
                json.has("application_id") ? () -> json.getString("application_id") : null,
                json.has("user") ? User.decompile(json.getJSONObject("user"), discordJar) : null,
                discordJar
        );
    }

    public void sendMessage(String m) {
        try {
            DiscordResponse response = new DiscordRequest(
                    new JSONObject()
                            .put("content", m),
                    new HashMap<>(),
                    URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                    discordJar,
                    URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                    RequestMethod.POST
            ).invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmbeds(Embed... embeds) {
        if (embeds.length > 10) {
            throw new IllegalArgumentException("There can be no greater than 10 embeds!");
        }
        try {
            DiscordResponse response = new DiscordRequest(
                    new JSONObject()
                            .put("embeds", new JSONArray(embeds)),
                    new HashMap<>(),
                    URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                    discordJar,
                    URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                    RequestMethod.POST
            ).invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
