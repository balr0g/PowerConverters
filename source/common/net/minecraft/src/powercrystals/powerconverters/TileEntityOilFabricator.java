package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.BuildCraftEnergy;

public class TileEntityOilFabricator extends TileEntityLiquidFab
{
	public TileEntityOilFabricator()
	{
		super(PowerConverterCore.oilUnitCostInEU * 5, BuildCraftEnergy.oilStill.blockID, PowerConverterCore.oilUnitCostInEU);
	}
}
