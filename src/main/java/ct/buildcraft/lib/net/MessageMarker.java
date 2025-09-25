/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import ct.buildcraft.api.core.BCLog;
import ct.buildcraft.lib.marker.MarkerCache;
import ct.buildcraft.lib.misc.MessageUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageMarker {
    private static final boolean DEBUG = MessageManager.DEBUG;

    public boolean add, multiple, connection;
    public int cacheId, count;
    public final List<BlockPos> positions = new ArrayList<>();

    public MessageMarker() {}
    
    public MessageMarker(FriendlyByteBuf buf) {
        PacketBufferBC packet = PacketBufferBC.asPacketBufferBc(buf);
        add = packet.readBoolean();
        multiple = packet.readBoolean();
        connection = packet.readBoolean();
        cacheId = packet.readShort();
        if (multiple) {
            count = packet.readShort();
        } else {
            count = 1;
        }
        
        for (int i = 0; i < count; i++) {
            positions.add(MessageUtil.readBlockPos(packet));
        }
    }

    
    public static void toBytes(MessageMarker msg, FriendlyByteBuf buf) {
        msg.count = msg.positions.size();
        msg.multiple = msg.count != 1;
        PacketBufferBC packet = PacketBufferBC.asPacketBufferBc(buf);
        packet.writeBoolean(msg.add);
        packet.writeBoolean(msg.multiple);
        packet.writeBoolean(msg.connection);
        packet.writeShort(msg.cacheId);
        if (msg.multiple) {
            packet.writeShort(msg.count);
        }
        for (BlockPos pos : msg.positions) {
            MessageUtil.writeBlockPos(packet, pos);
        }
    }

    @Override
    public String toString() {
        boolean[] flags = { add, multiple, connection };
        return "Message Marker [" + Arrays.toString(flags) + ", cacheId " + cacheId + ", count = " + count
            + ", positions = " + positions + "]";
    }

    public static final BiConsumer<MessageMarker, Supplier<NetworkEvent.Context>> HANDLER = (message, ctx) -> {
    	if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            if (DEBUG) {
                BCLog.logger.warn("[lib.messages][marker] Recived invaild message from client!");
            }
            ctx.get().setPacketHandled(true);
            return;
    	}
    	Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;
        if (world == null) {
            if (DEBUG) {
                BCLog.logger.warn("[lib.messages][marker] The world was null for a message!");
            }
            ctx.get().setPacketHandled(true);
            return;
        }
        if (message.cacheId < 0 || message.cacheId >= MarkerCache.CACHES.size()) {
            if (DEBUG) {
                BCLog.logger.warn("[lib.messages][marker] The cache ID " + message.cacheId + " was invalid!");
            }
            return;
        }
        MarkerCache<?> cache = MarkerCache.CACHES.get(message.cacheId);
//        BCLog.logger.error("MessageMarker:FAIL to handle message!");
        cache.getSubCache(world).handleMessageMain(message);
        ctx.get().setPacketHandled(true);
    };
}
