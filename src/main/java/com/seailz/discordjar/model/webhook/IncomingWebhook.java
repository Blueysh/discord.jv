package com.seailz.discordjar.model.webhook;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.core.Compilerable;
import com.seailz.discordjar.model.embed.Embed;
import com.seailz.discordjar.model.message.Message;
import com.seailz.discordjar.model.user.User;
import com.seailz.discordjar.utils.Checker;
import com.seailz.discordjar.utils.Snowflake;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.HashMap;

/**
 * Represents an Incoming Webhook.
 * @param name The name of the Webhook.
 * @param type The type of the Webhook.
 * @param channelId The id of the Channel the Webhook is in.
 * @param token The token of the Webhook.
 * @param avatar The avatar of the Webhook.
 * @param guildId The id of the Guild the Webhook is in.
 * @param id The id of the Webhook.
 * @param applicationId The application id of the Webhook.
 * @param user The user object tied to the Webhook.
 * @param discordJar A discord.jar instance used internally.
 */
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

    /**
     * Sends a message as the Webhook.
     * @param messageContent Message content to send.
     */
    public void sendMessage(String messageContent) {
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("content", messageContent),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Sends a message as the Webhook.
     * @param messageContent Message content to send.
     * @param usernameOverride A username override to use when sending this message. The Webhook remains unaffected.
     */
    public void sendMessage(String messageContent, String usernameOverride) {
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("content", messageContent)
                        .put("username", usernameOverride),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Sends a list of {@link com.seailz.discordjar.model.embed.Embed} objects as the Webhook.
     * @param embeds The embeds to send.
     */
    public void sendEmbeds(Embed... embeds) {
        Checker.check(embeds.length <= 10, "There can be no greater than 10 embeds!");
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("embeds", new JSONArray(embeds)),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Sends a list of {@link com.seailz.discordjar.model.embed.Embed} objects as the Webhook.
     * @param embeds The embeds to send.
     * @param usernameOverride A username override to use when sending the embeds. The Webhook remains unaffected.
     */
    public void sendEmbeds(String usernameOverride, Embed... embeds) {
        Checker.check(embeds.length <= 10, "There can be no greater than 10 embeds!");
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("embeds", new JSONArray(embeds))
                        .put("username", usernameOverride),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Sends a message as the Webhook.
     * @param message The Message object to send.
     */
    public void send(Message message) {
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("content", message.content()),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Sends a message as the Webhook.
     * @param message The Message object to send.
     * @param usernameOverride A username override to use when sending the message. The Webhook remains unaffected.
     */
    public void send(Message message, String usernameOverride){
        DiscordRequest request = new DiscordRequest(
                new JSONObject()
                        .put("content", message.content())
                        .put("embeds", message.embeds())
                        .put("attachments", message.attachments())
                        .put("username", usernameOverride),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK.replace("{guild.id}", guildId().id()).replace("{channel.id}", channelId().id()).replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.POST.GUILDS.CHANNELS.EXECUTE_WEBHOOK,
                RequestMethod.POST
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Deletes this Webhook.
     */
    public void delete() {
        DiscordRequest request = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK_NO_TOKEN.replace("{webhook.id}", id.id()),
                discordJar,
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK_NO_TOKEN,
                RequestMethod.DELETE
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Deletes this Webhook, with token authentication.
     */
    public void deleteWithToken(@NotNull String token) {
        Checker.nullOrEmpty(token, "Token may not be empty or null!");
        DiscordRequest request = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK.replace("{webhook.id}", id.id()).replace("{webhook.token}", token),
                discordJar,
                URLS.DELETE.CHANNEL.DELETE_WEBHOOK,
                RequestMethod.DELETE
        );
        try {
            request.invoke();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }
}
