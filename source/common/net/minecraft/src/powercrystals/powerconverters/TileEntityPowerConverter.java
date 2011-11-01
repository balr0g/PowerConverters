package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergyTile;

public abstract class TileEntityPowerConverter extends TileEntity implements IEnergyTile
{
	protected boolean isAddedToEnergyNet = false;
	private boolean invalid = false;

	public void resetEnergyNetwork()
	{
		if(isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).removeTileEntity(this);
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
		}
	}
	
	@Override
	public boolean canUpdate()
	{
		return !invalid;
	}
	
	@Override
	public void validate()
	{
		invalid = false;
	}
	
	@Override
	public void invalidate()
	{
		invalid = true;
		if(isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).removeTileEntity(this);
			isAddedToEnergyNet = false;
		}
	}

	@Override
	public boolean isAddedToEnergyNet()
	{
		return isAddedToEnergyNet;
	}
	
	protected boolean isRedstonePowered()
	{
		if(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
		{
			return true;
		}
		for(BlockPosition bp : new BlockPosition(this).getAdjacent(false))
		{
			int blockId = worldObj.getBlockId(bp.x, bp.y, bp.z);
			if(blockId == Block.redstoneWire.blockID && Block.blocksList[blockId].isPoweringTo(worldObj, bp.x, bp.y, bp.z, 1))
			{
				return true;
			}
		}
		return false;
	}
}
