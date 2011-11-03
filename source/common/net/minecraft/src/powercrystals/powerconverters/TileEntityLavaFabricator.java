package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;

public class TileEntityLavaFabricator extends TileEntityLiquidFab
{
	public TileEntityLavaFabricator()
	{
		super(PowerConverterCore.lavaUnitCostInEU * 5, Block.lavaStill.blockID, PowerConverterCore.lavaUnitCostInEU);
	}
}
