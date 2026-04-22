package ct.buildcraft.compat.ic2;

import ct.buildcraft.compat.CompatCapTransfromer;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;

public class Ic2Compat {

	public static void init() {
		CompatCapTransfromer.INSTANCE.registryFluidCapTransform(TileEntityGeoGenerator.class, (tile, d) -> new Ic2TankHandler(((InvSlotConsumableLiquidByTank)(((TileEntityGeoGenerator)tile).fluidSlot)).tank));
	}
}
