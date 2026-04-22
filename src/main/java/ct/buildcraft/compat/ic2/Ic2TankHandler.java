package ct.buildcraft.compat.ic2;

import org.jetbrains.annotations.NotNull;

import ic2.core.fluid.Ic2FluidStack;
import ic2.core.fluid.Ic2FluidTank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class Ic2TankHandler implements IFluidHandler{

	 protected final Ic2FluidTank tank;
	
	public Ic2TankHandler(Ic2FluidTank tank) {
		this.tank = tank;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tankid) {
		Ic2FluidStack ic2FluidStack = tank.getFluidStack();
		return new FluidStack(ic2FluidStack.getFluid(), ic2FluidStack.getAmountMb());
	}

	@Override
	public int getTankCapacity(int tankid) {
		return tank.getCapacity();
	}

	@Override
	public boolean isFluidValid(int tankid, @NotNull FluidStack stack) {
		return tank.fillMb(Ic2FluidStack.create(stack.getFluid(), stack.getAmount()), true) > 0;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return tank.fillMb(Ic2FluidStack.create(resource.getFluid(), resource.getAmount()), action.simulate());
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}

}
