package com.seailz.javadiscordwrapper.model.channel.forum;

import com.seailz.javadiscordwrapper.DiscordJv;
import com.seailz.javadiscordwrapper.core.Compilerable;
import com.seailz.javadiscordwrapper.model.channel.Channel;
import com.seailz.javadiscordwrapper.model.channel.utils.ChannelType;
import com.seailz.javadiscordwrapper.model.permission.PermissionOverwrite;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForumChannel extends Channel implements Compilerable {

    private ForumTag[] tags;
    private ForumTag[] appliedTags;
    private DefaultReaction defaultReaction;
    private DefaultSortOrder defaultSortOrder;

    public ForumChannel(
            String id,
            ChannelType type,
            String guildId,
            int position,
            PermissionOverwrite[] permissionOverwrites,
            String name,
            String topic,
            boolean nsfw,
            String lastMessageId,
            int rateLimitPerUser,
            String parentId,
            String lastPinTimestamp,
            String permissions,
            int defaultThreadRateLimitPerUser,
            ForumTag[] tags,
            ForumTag[] appliedTags,
            DefaultReaction defaultReaction,
            DefaultSortOrder defaultSortOrder,
            DiscordJv discordJv
    ) {
        super(id, type, guildId, position, permissionOverwrites, name, nsfw, parentId, permissions, discordJv);
        this.tags = tags;
        this.appliedTags = appliedTags;
        this.defaultReaction = defaultReaction;
        this.defaultSortOrder = defaultSortOrder;
    }

    @Override
    public JSONObject compile() {
        JSONArray tags = new JSONArray();
        for (ForumTag tag : this.tags) {
            tags.put(tag.compile());
        }

        JSONArray appliedTags = new JSONArray();
        for (ForumTag tag : this.appliedTags) {
            appliedTags.put(tag.compile());
        }

        return new JSONObject()
                .put("tags", tags)
                .put("applied_tags", appliedTags)
                .put("default_auto_archive_duration", defaultReaction.compile())
                .put("default_sort_order", defaultSortOrder.getCode())
                .put("id", id())
                .put("type", type().getCode())
                .put("guild_id", guildId())
                .put("position", position())
                .put("permission_overwrites", permissionOverwrites())
                .put("name", name())
                .put("nsfw", nsfw())
                .put("parent_id", parentId())
                .put("permissions", permissions());
    }


}