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

public class TileEntityEnergyLink extends TileEntityPowerConverter implements IEnergySink, IMachine
{
	private int storedEnergy = 0;
	private int maxStoredEnergy = 2500;
	//private int forwardSide;
	//private static Orientations[] orientationMap = new Orientations[6];
	
	/*static
	{
		orientationMap[0] = Orientations.YNeg;
		orientationMap[1] = Orientations.YPos;
		orientationMap[2] = Orientations.ZPos;
		orientationMap[3] = Orientations.ZNeg;
		orientationMap[4] = Orientations.XNeg;
		orientationMap[5] = Orientations.XPos;
	}*/
	
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
		int bcEnergyStored = storedEnergy * PowerConverterCore.icToBCScaleNumerator / PowerConverterCore.bcToICScaleDenominator;
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
					int energyUsed = Math.min(pp.maxEnergyReceived, bcEnergyStored);
					pp.receiveEnergy(energyUsed);
					storedEnergy -= energyUsed * PowerConverterCore.icToBCScaleDenominator / PowerConverterCore.bcToICScaleNumerator;
				}
			}
		}
		/*if(storedEnergy >= bcPulseSize * PowerConverterCore.icToBCScaleDenominator / PowerConverterCore.bcToICScaleNumerator)
		{
			Orientations o = findPowerPipe();
			if(o != null)
			{
				getPipeTransportFromOrientation(o).receiveEnergy(o.reverse(), bcPulseSize);
				storedEnergy -= bcPulseSize * PowerConverterCore.icToBCScaleDenominator / PowerConverterCore.bcToICScaleNumerator;
			}
		}*/
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
	
	/*public int getForwardSide()
	{
		return forwardSide;
	}
	
	public void rotate()
	{
		forwardSide++;
		if(forwardSide >= 6)
		{
			forwardSide = 0;
		}
	}
	
	public void rotateTo(int side)
	{
		forwardSide = side;
	}*/
	
	/*private PipeTransportPower getPipeTransportFromOrientation(Orientations o)
	{
		if(o == Orientations.XPos)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord)).pipe.transport;
		}
		if(o == Orientations.XNeg)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord)).pipe.transport;
		}
		if(o == Orientations.YPos)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord)).pipe.transport;
		}
		if(o == Orientations.YNeg)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord)).pipe.transport;
		}
		if(o == Orientations.ZPos)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1)).pipe.transport;
		}
		if(o == Orientations.ZNeg)
		{
			return (PipeTransportPower)((TileGenericPipe)worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1)).pipe.transport;
		}
		return null;
	}
	
	private Orientations findPowerPipe()
	{
		if(isPowerPipe(xCoord + 1, yCoord, zCoord))
		{
			return Orientations.XPos;
		}
		else if(isPowerPipe(xCoord - 1, yCoord, zCoord))
		{
			return Orientations.XNeg;
		}
		else if(isPowerPipe(xCoord, yCoord + 1, zCoord))
		{
			return Orientations.YPos;
		}
		else if(isPowerPipe(xCoord, yCoord - 1, zCoord))
		{
			return Orientations.YNeg;
		}
		else if(isPowerPipe(xCoord, yCoord, zCoord + 1))
		{
			return Orientations.ZPos;
		}
		else if(isPowerPipe(xCoord, yCoord, zCoord - 1))
		{
			return Orientations.ZNeg;
		}
		return null;
	}
	
	private boolean isPowerPipe(int x, int y, int z)
	{
		TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		if(te instanceof TileGenericPipe)
		{
			TileGenericPipe tgp = (TileGenericPipe)te;
			if(tgp.pipe != null && tgp.pipe.transport != null && tgp.pipe.transport instanceof PipeTransportPower)
			{
				return true;
			}
		}
		return false;
	}*/
	
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
