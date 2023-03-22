package com.seailz.discordjar.model.channel;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.action.channel.invites.CreateChannelInviteAction;
import com.seailz.discordjar.model.channel.internal.GuildChannelImpl;
import com.seailz.discordjar.model.channel.utils.ChannelType;
import com.seailz.discordjar.model.guild.Guild;
import com.seailz.discordjar.model.permission.PermissionOverwrite;
import com.seailz.discordjar.model.webhook.IncomingWebhook;
import com.seailz.discordjar.model.webhook.Webhook;
import com.seailz.discordjar.utils.Checker;
import com.seailz.discordjar.utils.URLS;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.discordjar.utils.rest.DiscordResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface GuildChannel extends Channel {

    @NotNull
    Guild guild();

    // Will return 0 if not found
    int position();

    @Nullable
    List<PermissionOverwrite> permissionOverwrites();

    boolean nsfw();

    @NotNull JSONObject raw();

    @Override
    default JSONObject compile() {
        JSONObject obj = new JSONObject();
        obj.put("id", id());
        obj.put("type", type().getCode());
        obj.put("name", name());
        obj.put("guild_id", guild().id());
        obj.put("position", position());
        obj.put("nsfw", nsfw());

        if (permissionOverwrites() != null) {
            JSONArray array = new JSONArray();
            for (PermissionOverwrite overwrite : permissionOverwrites())
                array.put(overwrite.compile());
        }

        obj.put("permission_overwrites", permissionOverwrites());
        return obj;
    }

    /**
     * Decompile a {@link JSONObject} into a {@link GuildChannel}
     *
     * @param obj        The {@link JSONObject} to decompile
     * @param discordJar The {@link DiscordJar} instance
     * @return The {@link GuildChannel} instance
     */
    @NotNull
    @Contract("_, _ -> new")
    static GuildChannel decompile(@NotNull JSONObject obj, @NotNull DiscordJar discordJar) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        String id = obj.getString("id");
        ChannelType type = ChannelType.fromCode(obj.getInt("type"));
        String name = obj.getString("name");
        Guild guild = obj.has("guild_id") ? discordJar.getGuildById(obj.getString("guild_id")) : null;
        int position = obj.has("position") ? obj.getInt("position") : 0;
        boolean nsfw = obj.has("nsfw") && obj.getBoolean("nsfw");

        List<PermissionOverwrite> permissionOverwrites = new ArrayList<>();
        if (obj.has("permission_overwrites")) {
            JSONArray array = obj.getJSONArray("permission_overwrites");
            for (int i = 0; i < array.length(); i++) {
                JSONObject overwrite = array.getJSONObject(i);
                permissionOverwrites.add(PermissionOverwrite.decompile(overwrite));
            }
        }

        return new GuildChannelImpl(id, type, name, guild, position, permissionOverwrites, nsfw, obj, discordJar);
    }

    @NotNull
    DiscordJar discordJv();

    /**
     * Returns this class as a {@link MessagingChannel}, or null if it is not a messaging channel.
     *
     * @throws IllegalArgumentException If the channel is not a messaging channel
     */
    @Nullable
    default MessagingChannel asMessagingChannel() {
        try {
            return MessagingChannel.decompile(raw(), discordJv());
        } catch (Exception e) {
            Checker.check(true, "This channel is not a messaging channel");
        }
        return null;
    }

    /**
     * Adds a {@link PermissionOverwrite} to this channel
     *
     * @param overwrite The {@link PermissionOverwrite} to add
     */
    default void addPermissionOverwrite(@NotNull PermissionOverwrite overwrite) {
        permissionOverwrites().add(overwrite);
        modify().setPermissionOverwrites(permissionOverwrites()).run();
    }

    default CreateChannelInviteAction createInvite() {
        return new CreateChannelInviteAction(discordJv(), id());
    }

    /**
     * Creates an Incoming Webhook for this channel.
     *
     * @param name The name of the Webhook.
     * @return The created Webhook.
     * @implNote Avatars are not implemented yet.
     */
    default IncomingWebhook createIncomingWebhook(String name) throws DiscordRequest.UnhandledDiscordAPIErrorException {
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

    default Webhook getWebhookById(long id, String token) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = null;
        response = new DiscordRequest(
                new JSONObject(),
                new HashMap<>(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK.replace("{webhook.id}", String.valueOf(id)).replace("{webhook.token}", token),
                discordJv(),
                URLS.GET.GUILDS.CHANNELS.GET_CHANNEL_WEBHOOK,
                RequestMethod.GET
        ).invoke();
        return Webhook.decompile(response.body(), discordJv());

    }

    default Webhook getWebhookById(String id, String token) throws DiscordRequest.UnhandledDiscordAPIErrorException {
        DiscordResponse response = null;
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

    default List<Webhook> getWebhooks() throws DiscordRequest.UnhandledDiscordAPIErrorException {
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
