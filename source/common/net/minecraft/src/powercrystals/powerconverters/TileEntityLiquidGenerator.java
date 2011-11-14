package net.minecraft.src.powercrystals.powerconverters;

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

public abstract class TileEntityLiquidGenerator extends TileEntityPowerConverter implements IEnergySource, ILiquidContainer
{
	protected int liquidId;
	protected int liquidStored = 0;
	protected int liquidStoredMax;
	protected int liquidConsumedPerAction;
	protected boolean wasActive = false;
	protected int euStored = 0;
	protected int euStoredMax;
	protected int euPulseSize;
	protected int euPerLiquidConsumptionAction;
	
	protected TileEntityLiquidGenerator(int liquidId, int liquidConsumedPerAction, int euPerLiquidConsumptionAction, int euPulseSize)
	{
		this.liquidId = liquidId;
		this.liquidConsumedPerAction = liquidConsumedPerAction;
		this.euPerLiquidConsumptionAction = euPerLiquidConsumptionAction;
		this.euPulseSize = euPulseSize;
		this.liquidStoredMax = API.BUCKET_VOLUME * 5;
		this.euStoredMax = euPulseSize * 2;
	}
	
	public boolean isActive()
	{
		return liquidStored > 0;
	}
	
	public boolean isConnected(int side)
	{
		BlockPosition p = new BlockPosition(this);
		p.orientation = PowerConverterCore.getOrientationFromSide(side);
		p.moveForwards(1);
		TileEntity te = worldObj.getBlockTileEntity(p.x, p.y, p.z);
		return (te != null && (te instanceof ILiquidContainer || te instanceof IEnergyTile));
	}
	
	// client, for network sync
	public void setStoredLiquid(int quantity)
	{
		liquidStored = quantity;
	}
	
	// Base methods
	
	public Packet getDescriptionPacket()
	{
		return PowerConverterCore.proxy.getTileEntityPacket(this, new int[] { liquidStored }, null, null);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		// bail if client
		if(PowerConverterCore.proxy.isClient(worldObj))
		{
			return;
		}
		// reset energy network
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		// update active state
		if(wasActive != isActive())
		{
			worldObj.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
			wasActive = isActive();
			if(PowerConverterCore.proxy.isServer())
			{
				PowerConverterCore.proxy.sendTileEntityPacket(this);
			}
		}
		// consume liquid
		while(liquidStored >= liquidConsumedPerAction && euStored < euPulseSize)
		{
			liquidStored -= liquidConsumedPerAction;
			euStored += euPerLiquidConsumptionAction;
		}
		// send power if we have it
		if(euStored >= euPulseSize)
		{
			int powerNotTransmitted = EnergyNet.getForWorld(worldObj).emitEnergyFrom(this, euPulseSize);
			euStored -= (euPulseSize - powerNotTransmitted);
		}
		euStored = Math.min(euStored, euStoredMax);
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		liquidStored = nbttagcompound.getInteger("liquidStored");
		euStored = nbttagcompound.getInteger("euStored");
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("liquidStored", liquidStored);
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
		return Math.min(liquidStored, liquidConsumedPerAction) * euPerLiquidConsumptionAction;
	}
	
	// ILiquidContainer methods

	@Override
	public int fill(Orientations from, int quantity, int id, boolean doFill)
	{
		if(id != getLiquidId())
		{
			return 0;
		}
		int amountToFill = Math.min(quantity, liquidStoredMax - liquidStored);
		if(doFill)
		{
			liquidStored += amountToFill;
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
		return liquidStored;
	}

	@Override
	public int getCapacity()
	{
		return liquidStoredMax;
	}

	@Override
	public int getLiquidId()
	{
		return liquidId;
	}
}
