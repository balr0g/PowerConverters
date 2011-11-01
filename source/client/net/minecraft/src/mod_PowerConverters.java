package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;

public class mod_PowerConverters extends BaseModMp
{
	public mod_PowerConverters()
	{
		PowerConverterCore.init(Minecraft.getMinecraftDir() + "/config/PowerConverters.cfg");
		
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 0), "Engine Generator (LV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 1), "Engine Generator (MV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 2), "Engine Generator (HV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 3), "Oil Fabricator");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 4), "Energy Link");
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
		
		MinecraftForgeClient.preloadTexture(PowerConverterCore.terrainTexture);
		
		ModLoaderMp.Init();
	}
}
