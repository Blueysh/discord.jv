package com.seailz.discordjar.model.channel.internal;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.model.channel.GuildChannel;
import com.seailz.discordjar.model.channel.utils.ChannelType;
import com.seailz.discordjar.model.guild.Guild;
import com.seailz.discordjar.model.permission.PermissionOverwrite;
import com.seailz.discordjar.model.webhook.IncomingWebhook;
import com.seailz.discordjar.model.webhook.Webhook;
import com.seailz.discordjar.utils.Checker;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.DiscordResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuildChannelImpl extends ChannelImpl implements GuildChannel {

    private final Guild guild;
    private final int position;
    private final List<PermissionOverwrite> permissionOverwrites;
    private final boolean nsfw;
    private final DiscordJar discordJar;

    public GuildChannelImpl(String id, ChannelType type, String name, Guild guild, int position, List<PermissionOverwrite> permissionOverwrites, boolean nsfw, JSONObject raw, DiscordJar discordJar) {
        super(id, type, name, raw, discordJar);
        this.guild = guild;
        this.position = position;
        this.permissionOverwrites = permissionOverwrites;
        this.nsfw = nsfw;
        this.discordJar = discordJar;
    }

    @Override
    public @NotNull Guild guild() {
        return this.guild;
    }

    // Will return 0 if not found
    @Override
    public int position() {
        return this.position;
    }

    @Nullable
    @Override
    public List<PermissionOverwrite> permissionOverwrites() {
        return this.permissionOverwrites;
    }

    @Override
    public boolean nsfw() {
        return nsfw;
    }

    @NotNull
    @Override
    public DiscordJar discordJv() {
        return this.discordJar;
    }

    /**
     * Creates an Incoming Webhook for this channel.
     *
     * @param name The name of the Webhook.
     * @return The created Webhook.
     * @implNote Avatars are not yet implemented in discord.jar.
     * @throws DiscordRequest.UnhandledDiscordAPIErrorException Thrown when an unexpected error is returned from the Discord API.
     */
    // FIXME: 3/23/23 Implement avatar data
    @Override public IncomingWebhook createWebhook(String name) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = new DiscordRequest(
                new JSONObject()
                        .put("name", name),
                new HashMap<>(),
                URLS.POST.GUILDS.CHANNELS.CREATE_WEBHOOK.replace("{guild.id}", guild().id()).replace("{channel.id}", id()),
                discordJv(),
                URLS.POST.GUILDS.CHANNELS.CREATE_WEBHOOK,
                RequestMethod.POST
        ).invoke();
        return IncomingWebhook.decompile(response.body(), discordJv());
    }

    /**
     * Gets a Webhook a channel has in effect.
     * @param id The id of the Webhook.
     * @param token The token of the Webhook.
     * @return A {@link com.seailz.discordjar.model.webhook.Webhook} object.
     * @throws DiscordRequest.UnhandledDiscordAPIErrorException Thrown when an unexpected error is returned from the Discord API.
     */
    @Override public Webhook getWebhookByIdWithToken(long id, @NotNull String token) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        return getWebhookByIdWithToken(String.valueOf(id), token);
    }

    /**
     * Gets a Webhook a channel has in effect.
     * @param id The id of the Webhook.
     * @param token The token of the Webhook.
     * @return A {@link com.seailz.discordjar.model.webhook.Webhook} object.
     * @throws DiscordRequest.UnhandledDiscordAPIErrorException Thrown when an unexpected error is returned from the Discord API.
     */
    @Override public Webhook getWebhookByIdWithToken(@NotNull String id, @NotNull String token) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response;
        Checker.nullOrEmpty(id, "ID string may not be empty or null.");
        Checker.nullOrEmpty(token, "Token string may not be empty or null.");
        response = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK.replace("{webhook.id}", id).replace("{webhook.token}", token),
                discordJv(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK,
                RequestMethod.GET
        ).invoke();
        return Webhook.decompile(response.body(), discordJv());
    }

    @Override public Webhook getWebhookById(long id) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        return getWebhookById(String.valueOf(id));
    }

    @Override public Webhook getWebhookById(@NotNull String id) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK_NO_TOKEN.replace("{webhook.id}", id),
                discordJar,
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK_NO_TOKEN,
                RequestMethod.GET
        ).invoke();

        return Webhook.decompile(response.body(), discordJar);
    }

    /**
     * Gets the Webhooks that the channel has in effect.
     * @return A {@link java.util.List} of {@link com.seailz.discordjar.model.webhook.Webhook} objects.
     * @throws DiscordRequest.UnhandledDiscordAPIErrorException Thrown when an unexpected error is returned from the Discord API.
     */
    @Override public List<Webhook> getWebhooks() throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOKS.replace("{guild.id}", guild().id()).replace("{channel.id}", id()),
                discordJv(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOKS,
                RequestMethod.GET
        ).invoke();
        List<Webhook> webhooks = new ArrayList<>();
        response.arr().forEach(h -> webhooks.add(Webhook.decompile((JSONObject) h, discordJv())));
        return webhooks;
    }
}
