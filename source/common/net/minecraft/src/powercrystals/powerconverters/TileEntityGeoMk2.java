package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;

public class TileEntityGeoMk2 extends TileEntityLiquidGenerator
{
	public TileEntityGeoMk2()
	{
		super(Block.lavaStill.blockID, 1, PowerConverterCore.euProducedPerLavaUnit, PowerConverterCore.euPerSecondLava);
	}
}
