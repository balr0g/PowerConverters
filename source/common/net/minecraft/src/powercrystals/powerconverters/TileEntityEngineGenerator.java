package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.api.IPowerReceptor;
import net.minecraft.src.buildcraft.api.PowerFramework;
import net.minecraft.src.buildcraft.api.PowerProvider;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.EnergyNet;
import net.minecraft.src.ic2.api.IEnergySource;

public class TileEntityEngineGenerator extends TileEntityPowerConverter implements IPowerReceptor, IEnergySource
{
	private PowerProvider powerProvider;
	private int storedPower;
	private int maxStoredPower;
	private int pulseSize;
	
	public TileEntityEngineGenerator()
	{
		setPowerProvider(PowerFramework.currentFramework.createPowerProvider());
		this.pulseSize = 30;
		this.maxStoredPower = 100;
		setupPowerProvider();
	}
	
	public TileEntityEngineGenerator(int pulseSize, int maxStoredPower)
	{
		setPowerProvider(PowerFramework.currentFramework.createPowerProvider());
		this.pulseSize = pulseSize;
		this.maxStoredPower = maxStoredPower;
		setupPowerProvider();
	}
	
	private void setupPowerProvider()
	{
		getPowerProvider().configure(0, this.pulseSize, this.pulseSize * 5, 25, this.maxStoredPower);
	}
	
	// Base methods
	
	@Override
	public void updateEntity()
	{
		getPowerProvider().update(this);
		if(!isAddedToEnergyNet())
		{
			EnergyNet.getForWorld(worldObj).addTileEntity(this);
			isAddedToEnergyNet = true;
		}
		
		int used = pulseSize * PowerConverterCore.bcToICScaleDenominator / PowerConverterCore.bcToICScaleNumerator;
		
		if(used <= storedPower)
		{
			int output = pulseSize;
			storedPower -= used;
			int powerNotTransmitted = EnergyNet.getForWorld(worldObj).emitEnergyFrom(this, output);
			int powerReturned = (powerNotTransmitted * PowerConverterCore.bcToICScaleDenominator / PowerConverterCore.bcToICScaleNumerator);
			storedPower = Math.min(storedPower + powerReturned, maxStoredPower);
		}
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		super.readFromNBT(nbttagcompound);
		storedPower = nbttagcompound.getInteger("storedPower");
		maxStoredPower = nbttagcompound.getInteger("maxStoredPower");
		pulseSize = nbttagcompound.getInteger("pulseSize");
		setupPowerProvider();
    }
	
	@Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("storedPower", storedPower);
		nbttagcompound.setInteger("maxStoredPower", maxStoredPower);
		nbttagcompound.setInteger("pulseSize", pulseSize);
    }
	
	// IPowerReceptor methods
	
	@Override
	public void setPowerProvider(PowerProvider powerprovider)
	{
		this.powerProvider = powerprovider;
	}

	@Override
	public PowerProvider getPowerProvider()
	{
		return powerProvider;
	}

	@Override
	public void doWork()
	{
		if(storedPower < maxStoredPower)
		{
			int energy = powerProvider.useEnergy(1, maxStoredPower - storedPower, true);
			storedPower += energy;
		}
	}

	@Override
	public int powerRequest()
	{
		return getPowerProvider().maxEnergyReceived;
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
		return storedPower * PowerConverterCore.bcToICScaleNumerator / PowerConverterCore.bcToICScaleDenominator;
	}
}
