package ct.buildcraft.compat;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class Fluid2ItemCapWrapper implements IItemHandler{

	@Override
	public int getSlots() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSlotLimit(int slot) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

}
