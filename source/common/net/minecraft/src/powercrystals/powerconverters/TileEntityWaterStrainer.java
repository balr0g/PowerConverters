package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.Orientations;

public class TileEntityWaterStrainer extends TileEntityLiquidGenerator
{
	public TileEntityWaterStrainer()
	{
		super(Block.waterStill.blockID, PowerConverterCore.waterConsumedPerOutput, PowerConverterCore.euProducedPerWaterUnit, PowerConverterCore.euPerSecondWater);
	}

	@Override
	public int fill(Orientations from, int quantity, int id, boolean doFill)
	{
		if(id == Block.waterStill.blockID)
		{
			int amountToFill;
			if(isRedstonePowered())
			{
				amountToFill = quantity;
			}
			else
			{
				amountToFill = Math.min(quantity, liquidStoredMax - liquidStored);
			}
			if(doFill)
			{
				liquidStored += amountToFill;
			}
			return amountToFill;
		}
		else
		{
			BlockPosition p = new BlockPosition(this);
			p.orientation = from.reverse();
			p.moveForwards(1);
			TileEntity te = worldObj.getBlockTileEntity(p.x, p.y, p.z);
			if(te != null && te instanceof ILiquidContainer)
			{
				return ((ILiquidContainer)te).fill(from, quantity, id, doFill);
			}
			return 0;
		}
	}
	
	@Override
	public int getLiquidId()
	{
		return 0;
	}
}
