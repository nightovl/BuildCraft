package ct.buildcraft.lib.gui;

import ct.buildcraft.api.transport.pipe.IPipeHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public abstract class ContainerPipe extends MenuBC_Neptune {

    public final IPipeHolder pipeHolder;

    public ContainerPipe(Inventory playerInventory, MenuType<?> type, int id, ContainerLevelAccess access) {
        super(playerInventory, type, id);
        this.pipeHolder = access.evaluate(ContainerPipe::getPipeHolder).get();
    }

	@Override
	public boolean stillValid(Player player) {
		return pipeHolder.canPlayerInteract(player);
	}
	
	public static IPipeHolder getPipeHolder(Level level, BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof IPipeHolder holder)
			return holder;
		return null;
	}
}
