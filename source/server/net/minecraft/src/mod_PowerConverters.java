package net.minecraft.src;

import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;

public class mod_PowerConverters extends BaseModMp
{
	public mod_PowerConverters()
	{
		PowerConverterCore.init("config/PowerConverters.cfg");
	}
	
	@Override
	public String Version()
	{
		return "1.8.1R1.0.4";
	}
	
	@Override
	public void ModsLoaded()
	{
		PowerConverterCore.afterModsLoaded();
	}
}
