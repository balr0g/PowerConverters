package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.API;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergySource;
import net.minecraft.src.ic2.api.IEnergyTile;

public class TileEntityWaterStrainer extends TileEntityPowerConverter implements IEnergySource, ILiquidContainer
{
	private int waterStored;
	private int maxWaterStored = API.BUCKET_VOLUME * 5;
	private int waterPulseSize;
	private int euPulseSize;
	private int waterConsumedForThisOutput;
	private boolean wasActive;
	
	public TileEntityWaterStrainer()
	{
		waterPulseSize = 5;
		euPulseSize = waterPulseSize * PowerConverterCore.euProducedPerWaterUnit;
		waterConsumedForThisOutput = 0;
		waterStored = 0;
	}
	
	public boolean isActive()
	{
		return waterStored >= waterPulseSize;
	}
	
	// for network sync, client only
	public void setWaterDirectly(int water)
	{
		waterStored = water;
	}
	
	public boolean isConnected(int side)
	{
		BlockPosition p = new BlockPosition(this);
		p.orientation = PowerConverterCore.getOrientationFromSide(side);
		p.moveForwards(1);
		TileEntity te = worldObj.getBlockTileEntity(p.x, p.y, p.z);
		return (te != null && (te instanceof ILiquidContainer || te instanceof IEnergyTile));
	}
	
	public Packet getDescriptionPacket()
	{
		return PowerConverterCore.proxy.getTileEntityPacket(this, new int[] { waterStored }, null, null);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(PowerConverterCore.proxy.isClient(worldObj))
		{
			return;
		}
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		if(wasActive != isActive())
		{
			worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			wasActive = isActive();
			if(PowerConverterCore.proxy.isServer())
			{
				PowerConverterCore.proxy.sendTileEntityPacket(this);
			}
		}
		
		if(waterStored >= waterPulseSize)
		{
			waterStored -= waterPulseSize;
			waterConsumedForThisOutput += waterPulseSize;
		}
		
		if(waterConsumedForThisOutput >= PowerConverterCore.waterConsumedPerOutput * waterPulseSize)
		{
			EnergyNet.getForWorld(worldObj).emitEnergyFrom(this, euPulseSize);
			waterConsumedForThisOutput = 0;
		}
		
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		waterStored = nbttagcompound.getInteger("waterStored");
		waterConsumedForThisOutput = nbttagcompound.getInteger("waterConsumed");
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("waterStored", waterStored);
		nbttagcompound.setInteger("waterConsumed", waterConsumedForThisOutput);
    }
	
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction)
	{
		return true;
	}

	@Override
	public int getMaxEnergyOutput()
	{
		return Math.min(waterStored, waterPulseSize) * PowerConverterCore.euProducedPerWaterUnit;
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
				amountToFill = Math.min(quantity, maxWaterStored - waterStored);
			}
			if(doFill)
			{
				waterStored += amountToFill;
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
	public int empty(int quantityMax, boolean doEmpty)
	{
		return 0;
	}

	@Override
	public int getLiquidQuantity()
	{
		return 0;
	}

	@Override
	public int getCapacity()
	{
		return 0;
	}

	@Override
	public int getLiquidId()
	{
		return 0;
	}

}
