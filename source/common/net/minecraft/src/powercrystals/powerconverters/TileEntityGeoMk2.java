package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.API;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergySource;
import net.minecraft.src.ic2.api.IEnergyTile;

public class TileEntityGeoMk2 extends TileEntityPowerConverter implements ILiquidContainer, IEnergySource
{
	private int currentLiquidStored;
	private int maxLiquidStored;
	private boolean wasActive;
	
	public TileEntityGeoMk2()
	{
		currentLiquidStored = 0;
		maxLiquidStored = API.BUCKET_VOLUME * 5;
	}
	
	public boolean isActive()
	{
		return currentLiquidStored > 0;
	}
	
	public boolean isConnected(int side)
	{
		BlockPosition p = new BlockPosition(this);
		p.orientation = PowerConverterCore.getOrientationFromSide(side);
		p.moveForwards(1);
		TileEntity te = worldObj.getBlockTileEntity(p.x, p.y, p.z);
		return (te != null && (te instanceof ILiquidContainer || te instanceof IEnergyTile));
	}
	
	// Base methods
	
	@Override
	public void updateEntity()
	{
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		if(wasActive != isActive())
		{
			worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			wasActive = isActive();
		}
		
		int pulseSize = PowerConverterCore.euProducedPerLavaUnit;
		
		if(currentLiquidStored > 0)
		{
			int powerNotTransmitted = EnergyNet.getForWorld(worldObj).emitEnergyFrom(this, pulseSize);
			if(powerNotTransmitted < pulseSize)
			{
				currentLiquidStored--;
			}
		}
		
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		currentLiquidStored = nbttagcompound.getInteger("liquidStored");
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("liquidStored", currentLiquidStored);
    }
	
	// IEnergySource methods
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction)
	{
		return true;
	}

	@Override
	public int getMaxEnergyOutput()
	{
		return currentLiquidStored * PowerConverterCore.euProducedPerLavaUnit;
	}

	// ILiquidContainer methods
	
	@Override
	public int fill(Orientations from, int quantity, int id, boolean doFill)
	{
		if(id != getLiquidId())
		{
			return 0;
		}
		int amountToFill = Math.min(quantity, maxLiquidStored - currentLiquidStored);
		if(doFill)
		{
			currentLiquidStored += amountToFill;
		}
		return amountToFill;
	}

	@Override
	public int empty(int quantityMax, boolean doEmpty)
	{
		return 0;
	}

	@Override
	public int getLiquidQuantity()
	{
		return currentLiquidStored;
	}

	@Override
	public int getCapacity()
	{
		return maxLiquidStored;
	}

	@Override
	public int getLiquidId()
	{
		return Block.lavaStill.blockID;
	}
}
