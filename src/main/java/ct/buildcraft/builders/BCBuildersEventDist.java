/*
 * Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package ct.buildcraft.builders;

import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import ct.buildcraft.api.schematics.ISchematicBlock;
import ct.buildcraft.builders.client.ClientArchitectTables;
import ct.buildcraft.builders.item.ItemSchematicSingle;
import ct.buildcraft.builders.item.ItemSnapshot;
import ct.buildcraft.builders.snapshot.Blueprint;
import ct.buildcraft.builders.snapshot.Snapshot;
import ct.buildcraft.builders.snapshot.Snapshot.Header;
import ct.buildcraft.builders.tile.TileQuarry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BCBuildersEventDist {

    private static final UUID UUID_SINGLE_SCHEMATIC = new UUID(0xfd3b8c59b0a8b191L, 0x772ec006c1b0ffaaL);
    private static final Map<Level, Deque<WeakReference<TileQuarry>>> allQuarries = new WeakHashMap<>();

    @Deprecated
    public static synchronized void validateQuarry(TileQuarry quarry) {
        Deque<WeakReference<TileQuarry>> quarries =
            allQuarries.computeIfAbsent(quarry.getLevel(), k -> new LinkedList<>());
        quarries.add(new WeakReference<>(quarry));
    }

    @Deprecated
    public static synchronized void invalidateQuarry(TileQuarry quarry) {
        Deque<WeakReference<TileQuarry>> quarries = allQuarries.get(quarry.getLevel());
        if (quarries == null) {
            // Odd.
            return;
        }
        Iterator<WeakReference<TileQuarry>> iter = quarries.iterator();
        while (iter.hasNext()) {
            WeakReference<TileQuarry> ref = iter.next();
            TileQuarry pos = ref.get();
            if (pos == null || pos == quarry) {
                iter.remove();
            }
        }
    }

/*    @SubscribeEvent
    public synchronized void onGetCollisionBoxesForQuarry(GetCollisionBoxesEvent event) {
        Deque<WeakReference<TileQuarry>> quarries = allQuarries.get(event.getWorld());
        Level
        if (quarries == null) {
            // No quarries in the target world
            return;
        }
        Iterator<WeakReference<TileQuarry>> iter = quarries.iterator();
        while (iter.hasNext()) {
            WeakReference<TileQuarry> ref = iter.next();
            TileQuarry quarry = ref.get();
            if (quarry == null) {
                iter.remove();
                continue;
            }
            for (AABB aabb : quarry.getCollisionBoxes()) {
                if (event.getAabb().intersects(aabb)) {
                    event.getCollisionBoxesList().add(aabb);
                }
            }
        }
    }*/

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderTooltipPostText(RenderTooltipEvent.Color event) {
        Snapshot snapshot = null;
        ItemStack stack = event.getItemStack();
        Header header = BCBuildersItems.BLUEPRINT.get() != null &&  BCBuildersItems.TEMPLATE.get() != null? ItemSnapshot.getHeader(stack) : null;
        if (header != null) {
            snapshot = ClientSnapshots.INSTANCE.getSnapshot(header.key);
        } else if (BCBuildersItems.SCHEMATIC_SINGLE.get() != null) {
            ISchematicBlock schematicBlock = ItemSchematicSingle.getSchematicSafe(stack);
            if (schematicBlock != null) {
                Blueprint blueprint = new Blueprint();
                blueprint.size = new BlockPos(1, 1, 1);
                blueprint.offset = BlockPos.ZERO;
                blueprint.data = new int[] { 0 };
                blueprint.palette.add(schematicBlock);
                blueprint.computeKey();
                snapshot = blueprint;
            }
        }

        if (snapshot != null) {
            int pX = event.getX();
            int pY = event.getY() + event.get() + 10;
            int sX = 100;
            int sY = 100;

            // Copy from GuiUtils#drawHoveringText
            int zLevel = 300;
            int backgroundColor = 0xF0100010;
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY - 4, pX + sX + 3, pY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY + sY + 3, pX + sX + 3, pY + sY + 4, backgroundColor,
                backgroundColor);
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY - 3, pX + sX + 3, pY + sY + 3, backgroundColor,
                backgroundColor);
            GuiUtils.drawGradientRect(zLevel, pX - 4, pY - 3, pX - 3, pY + sY + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(zLevel, pX + sX + 3, pY - 3, pX + sX + 4, pY + sY + 3, backgroundColor,
                backgroundColor);
            int borderColorStart = 0x505000FF;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY - 3 + 1, pX - 3 + 1, pY + sY + 3 - 1, borderColorStart,
                borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, pX + sX + 2, pY - 3 + 1, pX + sX + 3, pY + sY + 3 - 1, borderColorStart,
                borderColorEnd);
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY - 3, pX + sX + 3, pY - 3 + 1, borderColorStart,
                borderColorStart);
            GuiUtils.drawGradientRect(zLevel, pX - 3, pY + sY + 2, pX + sX + 3, pY + sY + 3, borderColorEnd,
                borderColorEnd);

            ClientSnapshots.INSTANCE.renderSnapshot(snapshot, pX, pY, sX, sY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTickClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().isPaused()) {
            ClientArchitectTables.tick();
        }
    }
}
