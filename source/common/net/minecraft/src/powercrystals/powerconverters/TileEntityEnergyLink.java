package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.IPowerReceptor;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.PowerProvider;
import net.minecraft.src.buildcraft.core.IMachine;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergySink;
import net.minecraft.src.ic2.api.IEnergyTile;

public class TileEntityEnergyLink extends TileEntityPowerConverter implements IEnergySink, IMachine
{
	private int storedEnergy = 0;
	private int maxStoredEnergy = 2500;
	
	public boolean isConnected(int side)
	{
		BlockPosition p = new BlockPosition(this);
		p.orientation = PowerConverterCore.getOrientationFromSide(side);
		p.moveForwards(1);
		TileEntity te = worldObj.getBlockTileEntity(p.x, p.y, p.z);
		if(te != null && (te instanceof IPowerReceptor && ((IPowerReceptor)te).getPowerProvider() != null) || (te instanceof IEnergyTile))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void updateEntity()
	{
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		if(isRedstonePowered())
		{
			return;
		}
		int bcEnergyStored = storedEnergy * PowerConverterCore.icToBCScaleNumerator / PowerConverterCore.icToBCScaleDenominator;
		BlockPosition ourbp = new BlockPosition(this);
		for(int i = 0; i < 6; i++)
		{
			Orientations o = Orientations.values()[i];
			BlockPosition bp = new BlockPosition(ourbp);
			bp.orientation = o;
			bp.moveForwards(1);
			TileEntity te = worldObj.getBlockTileEntity(bp.x, bp.y, bp.z);
			if(te != null && te instanceof IPowerReceptor)
			{
				PowerProvider pp = ((IPowerReceptor)te).getPowerProvider();
				if(pp != null && pp.preConditions((IPowerReceptor)te) && pp.minEnergyReceived <= bcEnergyStored)
				{
					int energyUsed = Math.min(Math.min(pp.maxEnergyReceived, bcEnergyStored), pp.maxEnergyStored - pp.energyStored);
					pp.receiveEnergy(energyUsed);
					storedEnergy -= energyUsed * PowerConverterCore.icToBCScaleDenominator / PowerConverterCore.icToBCScaleNumerator;
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

	// IMachine methods
	
	@Override
	public boolean isActive()
	{
		return false;
	}

	@Override
	public boolean manageLiquids()
	{
		return false;
	}

	@Override
	public boolean manageSolids()
	{
		return false;
	}

}
