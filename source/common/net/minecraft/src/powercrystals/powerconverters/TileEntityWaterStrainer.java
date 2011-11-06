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

public class TileEntityWaterStrainer extends TileEntityPowerConverter implements IEnergySource, ILiquidContainer
{
	private int waterStored;
	private int maxWaterStored = API.BUCKET_VOLUME * 5;
	private int waterPulseSize = 5;
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		/*if(wasActive != isActive())
		{
			worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			wasActive = isActive();
			if(PowerConverterCore.proxy.isServer())
			{
				PowerConverterCore.proxy.sendTileEntityPacket(this);
			}
		}*/
		
		int pulseSize = waterPulseSize * PowerConverterCore.euProducedPerWaterUnit;
		
		if(waterStored > waterPulseSize)
		{
			int powerNotTransmitted = EnergyNet.getForWorld(worldObj).emitEnergyFrom(this, pulseSize);
			if(powerNotTransmitted < pulseSize)
			{
				waterStored -= waterPulseSize;
			}
		}
		
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		waterStored = nbttagcompound.getInteger("waterStored");
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("waterStored", waterStored);
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
