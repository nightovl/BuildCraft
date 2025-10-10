/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.lib.gui;

import ct.buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public abstract class ContainerBCTile<T extends TileBC_Neptune> extends MenuBC_Neptune {
    public final T tile;

    @SuppressWarnings("resource")
	public ContainerBCTile(Inventory playerInventory, MenuType<?> type, int id, T tile) {
        super(playerInventory, type, id);
        this.tile = tile;
        if (!tile.getLevel().isClientSide) {
            tile.onPlayerOpen(playerInventory.player);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        tile.onPlayerClose(player);
    }

    @Override
    public final boolean stillValid(Player player) {
        return tile.canInteractWith(player);
    }

	@Override
    public void broadcastChanges() {
        super.broadcastChanges();
        tile.sendNetworkGuiTick(playerInventory.player);
    }
}
