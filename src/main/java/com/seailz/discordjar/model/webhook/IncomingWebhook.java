package com.seailz.discordjar.model.webhook;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.action.webhook.EditWebhookMessageAction;
import com.seailz.discordjar.action.webhook.WebhookExecuteAction;
import com.seailz.discordjar.core.Compilerable;
import com.seailz.discordjar.model.embed.Embed;
import com.seailz.discordjar.model.message.Attachment;
import com.seailz.discordjar.model.message.Message;
import com.seailz.discordjar.model.user.User;
import com.seailz.discordjar.utils.Checker;
import com.seailz.discordjar.utils.Snowflake;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.DiscordResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.HashMap;
import java.util.List;

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
     * Executes the Webhook.
     * @param content The Message content to include. Can be skipped by setting to {@code null}.
     * @param embeds The Embeds to include. Can be skipped by setting to {@code null}.
     * @param attachments The Attachments to include. Can be skipped by setting to {@code null}.
     * @param usernameOverride A username override to use when sending the message. The Webhook remains unaffected. Can be skipped by setting to {@code null}.
     * @param avatarUrlOverride An avatar override URL to use when sending the message. The Webhook remains unaffected. Can be skipped by setting to {@code null}.
     * @param threadName The name of the Thread that will be created with this message. Requires the Webhook channel to be a Forum channel. Can be skipped by setting to {@code null}.
     */
    public WebhookExecuteAction execute(String content, List<Embed> embeds, List<Attachment> attachments, String usernameOverride, String avatarUrlOverride, String threadName){
        return new WebhookExecuteAction(content, embeds, attachments, usernameOverride, avatarUrlOverride, threadName, discordJar, channelId, id, guildId, token);
    }

    /**
     * Edits a previously-sent message from the Webhook.
     * @param content The Message content to include. Can be skipped by setting to {@code null}.
     * @param embeds The Embeds to include. Can be skipped by setting to {@code null}.
     * @param attachments The Attachments to include. Can be skipped by setting to {@code null}.
     * @param usernameOverride A username override to use when sending the message. The Webhook remains unaffected. Can be skipped by setting to {@code null}.
     * @param avatarUrlOverride An avatar override URL to use when sending the message. The Webhook remains unaffected. Can be skipped by setting to {@code null}.
     * @param threadName The name of the Thread that will be created with this message. Requires the Webhook channel to be a Forum channel. Can be skipped by setting to {@code null}.
     */
    public EditWebhookMessageAction editMessage(String messageId, String content, List<Embed> embeds, List<Attachment> attachments, String usernameOverride, String avatarUrlOverride, String threadName){
        return new EditWebhookMessageAction(messageId, content, embeds, attachments, usernameOverride, avatarUrlOverride, threadName, id, token, discordJar);
    }

    /**
     * Gets a previously-sent Webhook message.
     * @param messageId The id of the message.
     * @return The Webhook Message if successful.
     */
    public Message getMessage(String messageId) {
        try {
            DiscordResponse res = new DiscordRequest(
                    new JSONObject(),
                    new HashMap<>(),
                    URLS.GET.WEBHOOK.GET_WEBHOOK_MESSAGE.replace("{webhook.id}", id.id()).replace("{webhook.token}", token).replace("{message.id}", messageId),
                    discordJar,
                    URLS.GET.WEBHOOK.GET_WEBHOOK_MESSAGE,
                    RequestMethod.GET
            ).invoke();

            return Message.decompile(res.body(), discordJar);
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new DiscordRequest.DiscordAPIErrorException(e);
        }
    }

    /**
     * Deletes a previously-sent Webhook message.
     * @param messageId The id of the message.
     */
    public void deleteMessage(String messageId) {
        try {
            new DiscordRequest(
                    new JSONObject(),
                    new HashMap<>(),
                    URLS.DELETE.CHANNEL.DELETE_WEBHOOK_MESSAGE.replace("{webhook.id}", id.id()).replace("{webhook.token}", token).replace("{message.id}", messageId),
                    discordJar,
                    URLS.DELETE.CHANNEL.DELETE_WEBHOOK_MESSAGE,
                    RequestMethod.DELETE
            ).invoke();
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
