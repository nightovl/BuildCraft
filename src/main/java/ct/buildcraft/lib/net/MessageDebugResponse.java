/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import io.netty.buffer.ByteBuf;

import net.minecraftforge.network.NetworkEvent;

public class MessageDebugResponse {
    private final List<String> left = new ArrayList<>();
    private final List<String> right = new ArrayList<>();

    public MessageDebugResponse() {}

    public MessageDebugResponse(List<String> left, List<String> right) {
        this.left.addAll(left);
        this.right.addAll(right);
    }

    public static void toBytes(MessageDebugResponse msg, ByteBuf buffer) {
        PacketBufferBC buf = PacketBufferBC.asPacketBufferBc(buffer);
        buf.writeInt(msg.left.size());
        msg.left.forEach(buf::writeUtf);
        buf.writeInt(msg.right.size());
        msg.right.forEach(buf::writeUtf);
    }

    public MessageDebugResponse(ByteBuf buffer) {
        PacketBufferBC buf = PacketBufferBC.asPacketBufferBc(buffer);
        IntStream.range(0, buf.readInt())
            .mapToObj(i -> new PacketBufferBC(buf).readString())
            .forEach(left::add);
        IntStream.range(0, buf.readInt())
            .mapToObj(i -> new PacketBufferBC(buf).readString())
            .forEach(right::add);
    }

    public static final BiConsumer<MessageDebugResponse, Supplier<NetworkEvent.Context>> HANDLER = (message, ctx) -> {
/*        ClientDebuggables.SERVER_LEFT.clear();
        ClientDebuggables.SERVER_LEFT.addAll(message.left);
        ClientDebuggables.SERVER_RIGHT.clear();
        ClientDebuggables.SERVER_RIGHT.addAll(message.right);
        return null;*/
    	ctx.get().setPacketHandled(true);
    };
}
