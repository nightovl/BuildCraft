package ct.buildcraft.lib.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;

public class BCContainerFactory<T extends MenuBC_Neptune> implements IContainerFactory<T>{
	
	final MenuType.MenuSupplier<T> constructor;
	
	public BCContainerFactory(MenuType.MenuSupplier<T> constructor) {
		this.constructor = constructor;
	}
	
	@Override
	public T create(int windowId, Inventory inv, FriendlyByteBuf data) {
		T containerArchitectTable = constructor.create(windowId, inv);
		if(data != null && data.isReadable())
			containerArchitectTable.clientInit(data);
		return containerArchitectTable;
	}

}
