package com.seailz.discordjar.action.webhook;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.model.embed.Embed;
import com.seailz.discordjar.model.message.Attachment;
import com.seailz.discordjar.model.message.Message;
import com.seailz.discordjar.utils.Snowflake;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;

public class EditWebhookMessageAction {
    private String messageId;
    private String content;
    private List<Embed> embeds;
    private List<Attachment> attachments;
    private String usernameOverride;
    private String avatarUrlOverride;
    private String threadName;
    private Snowflake webhookId;
    private String webhookToken;
    private final DiscordJar discordJar;

    public EditWebhookMessageAction(String messageId, String content, List<Embed> embeds, List<Attachment> attachments, String usernameOverride, String avatarUrlOverride, String threadName, Snowflake webhookId, String webhookToken, DiscordJar discordJar) {
        this.messageId = messageId;
        this.content = content;
        this.embeds = embeds;
        this.attachments = attachments;
        this.usernameOverride = usernameOverride;
        this.avatarUrlOverride = avatarUrlOverride;
        this.threadName = threadName;
        this.webhookId = webhookId;
        this.webhookToken = webhookToken;
        this.discordJar = discordJar;
    }

    public String messageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String content() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Embed> embeds() {
        return embeds;
    }

    public void setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
    }

    public List<Attachment> attachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String usernameOverride() {
        return usernameOverride;
    }

    public void setUsernameOverride(String usernameOverride) {
        this.usernameOverride = usernameOverride;
    }

    public String avatarUrlOverride() {
        return avatarUrlOverride;
    }

    public void setAvatarUrlOverride(String avatarUrlOverride) {
        this.avatarUrlOverride = avatarUrlOverride;
    }

    public String threadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Snowflake webhookId() {
        return webhookId;
    }

    public void setWebhookId(Snowflake webhookId) {
        this.webhookId = webhookId;
    }

    public String webhookToken() {
        return webhookToken;
    }

    public void setWebhookToken(String webhookToken) {
        this.webhookToken = webhookToken;
    }

    public DiscordJar discordJar() {
        return discordJar;
    }

    public Response<Message> run() {
        Response<Message> future = new Response<>();
        new Thread(() -> {
            JSONObject body = new JSONObject();
            if (content != null) body.put("content", content);
            if (embeds != null) body.put("embeds", new JSONArray(embeds));
            if (attachments != null) body.put("attachments", new JSONArray(attachments));
            if (usernameOverride != null) body.put("username", usernameOverride);
            if (avatarUrlOverride != null) body.put("avatar_url", avatarUrlOverride);
            if (threadName != null) {
                body.put("thread_name", threadName);
            }
            DiscordRequest request = new DiscordRequest(
                    body,
                    new HashMap<>(),
                    URLS.PATCH.WEBHOOK.EDIT_WEBHOOK_MESSAGE.replace("{webhook.id}", webhookId.id()).replace("{webhook.token}", webhookToken).replace("{message.id}", messageId),
                    discordJar,
                    URLS.PATCH.WEBHOOK.EDIT_WEBHOOK_MESSAGE,
                    RequestMethod.PATCH
            );
            try {
                future.complete(Message.decompile(request.invoke().body(), discordJar));
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
                future.completeError(new Response.Error(e));
            }
        }).start();

        return future;
    }
}
