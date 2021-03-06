package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergySink;

public abstract class TileEntityLiquidFab extends TileEntityPowerConverter implements IEnergySink, ILiquidContainer
{
	private int storedEnergy = 0;
	private int maxStoredEnergy = 1600;
	private int liquidId;
	private int liquidCost;
	
	protected TileEntityLiquidFab(int maxStoredEnergy, int liquidId, int liquidCost)
	{
		this.maxStoredEnergy = maxStoredEnergy;
		this.liquidId = liquidId;
		this.liquidCost = liquidCost;
	}
	// base methods
	
	@Override
	public void updateEntity()
	{
		if(PowerConverterCore.proxy.isClient(worldObj))
		{
			return;
		}
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		if(isRedstonePowered())
		{
			return;
		}
		if (storedEnergy >= liquidCost)
		{
			for (int i = 0; i < 6; ++i)
			{
				Position p = new Position(xCoord, yCoord, zCoord, Orientations.values()[i]);
				p.moveForwards(1);

				TileEntity tile = worldObj.getBlockTileEntity((int) p.x, (int) p.y,	(int) p.z);

				if(tile instanceof ILiquidContainer)
				{
					int liquidToProduce = storedEnergy / liquidCost;
					int liquidRemaining = liquidToProduce - ((ILiquidContainer) tile).fill(p.orientation.reverse(), liquidToProduce,
							liquidId, true);
					int liquidUsed = liquidToProduce - liquidRemaining;
					storedEnergy -= liquidUsed * liquidCost;
					
					if(liquidRemaining <= 0)
					{
						break;
					}
				}
			}
		}
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		storedEnergy = nbttagcompound.getInteger("storedEnergy");
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("storedEnergy", storedEnergy);
    }
	
	// IEnergySink methods

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction)
	{
		return true;
	}

	@Override
	public boolean demandsEnergy()
	{
		return storedEnergy < maxStoredEnergy;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount)
	{
		int amountToAdd = Math.min(amount, maxStoredEnergy - storedEnergy);
		storedEnergy += amountToAdd;
		return amount - amountToAdd;
	}
	
	// ILiquidContainer methods

	@Override
	public int fill(Orientations from, int quantity, int id, boolean doFill)
	{
		return 0;
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