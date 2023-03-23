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

/**
 * Represents a Webhook for a Channel.
 * @param id The id of the Webhook.
 * @param type The type of the Webhook.
 * @param guildId The id of the Guild the Webhook is in.
 * @param channelId The id of the Channel the Webhook is in.
 * @param user The user object tied to the Webhook.
 * @param name The name of the Webhook.
 * @param avatar The avatar of the Webhook.
 * @param token The token of the Webhook.
 * @param applicationId The application id of the Webhook.
 * @param sourceGuild The source Guild of the Webhook.
 * @param sourceChannel The source Channel of the Webhook.
 * @param url The URL of the Webhook.
 * @param discordJar A discord.jar instance used internally.
 */
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

    /**
     * @return This Webhook as an {@link com.seailz.discordjar.model.webhook.IncomingWebhook}.
     */
    public IncomingWebhook asIncomingWebhook() {
        return new IncomingWebhook(name, type, channelId, token, avatar, guildId, id, applicationId, user, discordJar);
    }

    /**
     * Deletes this Webhook.
     * @throws DiscordRequest.UnhandledDiscordAPIErrorException Thrown when an unexpected error is returned from the Discord API.
     */
    public void delete() throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK.replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK,
                RequestMethod.DELETE
        ).invoke();
    }
}
