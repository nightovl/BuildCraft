/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.chunkload;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import buildcraft.lib.misc.data.WorldPos;
import ct.buildcraft.api.core.BCLog;
import ct.buildcraft.lib.BCLib;
import ct.buildcraft.lib.BCLibConfig;
import ct.buildcraft.lib.misc.NBTUtilBC;
import net.minecraft.core.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.world.ForgeChunkManager;

public class ChunkLoaderManager {
    private static final Map<WorldPos, ForgeChunkManager.Ticket> TICKETS = new HashMap<>();

    /**
     * This should be called in {@link BlockEntity#validate()}, if a tile entity might be able to load. A check is
     * performed to see if the config allows it
     */
    public static <T extends BlockEntity & IChunkLoadingTile> void loadChunksForTile(T tile) {
        if (!canLoadFor(tile)) {
            releaseChunksFor(tile);
            return;
        }
        updateChunksFor(tile);
    }

    public static <T extends BlockEntity & IChunkLoadingTile> void releaseChunksFor(T tile) {
        ForgeChunkManager.releaseTicket(TICKETS.remove(new WorldPos(tile)));
    }

    private static <T extends BlockEntity & IChunkLoadingTile> void updateChunksFor(T tile) {
        WorldPos wPos = new WorldPos(tile);
        ForgeChunkManager.Ticket ticket = TICKETS.get(wPos);
        if (ticket == null) {
            ticket = ForgeChunkManager.requestTicket(
                BCLib.INSTANCE,
                tile.getWorld(),
                ForgeChunkManager.Type.NORMAL
            );
            if (ticket == null) {
                BCLog.logger.warn("[lib.chunkloading] Failed to chunkload " + tile.getClass().getName() + " at " + tile.getPos());
                return;
            }
            ticket.getModData().setTag("location", NBTUtilBC.writeBlockPos(tile.getPos()));
            TICKETS.put(wPos, ticket);
        }
        Set<ChunkPos> chunks = getChunksToLoad(tile);
        for (ChunkPos pos : ticket.getChunkList()) {
            if (!chunks.contains(pos)) {
                ForgeChunkManager.unforceChunk(ticket, pos);
            }
        }
        for (ChunkPos pos : chunks) {
            if (!ticket.getChunkList().contains(pos)) {
                ForgeChunkManager.forceChunk(ticket, pos);
            }
        }
    }

    public static <T extends BlockEntity & IChunkLoadingTile> Set<ChunkPos> getChunksToLoad(T tile) {
        Set<ChunkPos> chunksToLoad = tile.getChunksToLoad();
        Set<ChunkPos> chunkPoses = new HashSet<>(chunksToLoad != null ? chunksToLoad : Collections.emptyList());
        chunkPoses.add(new ChunkPos(tile.getPos()));
        return chunkPoses;
    }

    public static void rebindTickets(List<ForgeChunkManager.Ticket> tickets, World world) {
        TICKETS.clear();
        if (BCLibConfig.chunkLoadingLevel != BCLibConfig.ChunkLoaderLevel.NONE) {
            for (ForgeChunkManager.Ticket ticket : tickets) {
                BlockPos pos = NBTUtilBC.readBlockPos(ticket.getModData().getTag("location"));
                if (pos == null) {
                    ForgeChunkManager.releaseTicket(ticket);
                    continue;
                }
                WorldPos wPos = new WorldPos(world, pos);
                if (TICKETS.containsKey(wPos)) {
                    ForgeChunkManager.releaseTicket(ticket);
                    continue;
                }
                BlockEntity tile = world.getBlockEntity(pos);
                if (tile == null || !(tile instanceof IChunkLoadingTile) || !canLoadFor((IChunkLoadingTile) tile)) {
                    TICKETS.remove(wPos);
                    ForgeChunkManager.releaseTicket(ticket);
                    continue;
                }
                TICKETS.put(wPos, ticket);
                for (ChunkPos chunkPos : getChunksToLoad((BlockEntity & IChunkLoadingTile) tile)) {
                    ForgeChunkManager.forceChunk(ticket, chunkPos);
                }
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean canLoadFor(IChunkLoadingTile tile) {
        return BCLibConfig.chunkLoadingLevel.canLoad(tile.getLoadType());
    }
}
