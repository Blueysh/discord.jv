package com.seailz.discordjar.model.webhook;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.core.Compilerable;
import com.seailz.discordjar.model.channel.Channel;
import com.seailz.discordjar.model.guild.Guild;
import com.seailz.discordjar.model.user.User;
import com.seailz.discordjar.utils.Snowflake;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.DiscordResponse;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

public record Webhook(
        Snowflake id,
        int type,
        Snowflake guildId,
        Snowflake channelId,
        User user,
        String name,
        String avatar,
        String token,
        Snowflake applicationId,
        Guild sourceGuild,
        Channel sourceChannel,
        String url,
        DiscordJar discordJar
) implements Compilerable {
    @Override
    public JSONObject compile() {
        return new JSONObject()
                .put("id", id.id())
                .put("type", type)
                .put("guild_id", guildId.id())
                .put("channel_id", channelId.id())
                .put("user", user.compile())
                .put("name", name)
                .put("avatar", avatar)
                .put("token", token)
                .put("application_id", applicationId.id())
                .put("source_guild", sourceGuild.compile())
                .put("source_channel", sourceChannel.compile())
                .put("url", url);
    }

    public static Webhook decompile(JSONObject json, DiscordJar discordJar) {
        return new Webhook(
                json.has("id") ? () -> json.getString("id") : null,
                json.has("type") ? json.getInt("type") : 0,
                json.has("guild_id") ? () -> json.getString("guild_id") : null,
                json.has("channel_id") ? () -> json.getString("channel_id") : null,
                json.has("user") ? User.decompile(json.getJSONObject("user"), discordJar) : null,
                json.has("name") ? json.getString("name") : null,
                json.has("avatar") ? json.getString("avatar") : null,
                json.has("token") ? json.getString("token") : null,
                json.has("application_id") ? () -> json.getString("application_id") : null,
                json.has("source_guild") ? Guild.decompile(json.getJSONObject("source_guild"), discordJar) : null,
                json.has("source_channel") ? Channel.decompile(json.getJSONObject("source_channel"), discordJar) : null,
                json.has("url") ? json.getString("url") : null,
                discordJar
        );
    }

    public IncomingWebhook asIncomingWebhook() {
        return new IncomingWebhook(name, type, channelId, token, avatar, guildId, id, applicationId, user, discordJar);
    }

    public void delete() {
        try {
            DiscordResponse response = new DiscordRequest(
                    new JSONObject(),
                    new HashMap<>(),
                    URLS.DELETE.CHANNEL.DELETE_WEBHOOK.replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                    discordJar,
                    URLS.DELETE.CHANNEL.DELETE_WEBHOOK,
                    RequestMethod.DELETE
            ).invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
