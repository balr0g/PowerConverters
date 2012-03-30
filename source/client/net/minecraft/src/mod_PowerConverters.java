package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.powercrystals.powerconverters.IPCProxy;
import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;
import net.minecraft.src.powercrystals.powerconverters.TileEntityLiquidGenerator;

public class mod_PowerConverters extends BaseModMp
{
	public void load()
	{
		PowerConverterCore.init(new ClientProxy());
		
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 0), "Engine Generator (LV)");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 1), "Engine Generator (MV)");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 2), "Engine Generator (HV)");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 3), "Oil Fabricator");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 4), "Energy Link");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 5), "Lava Fabricator");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 6), "Geothermal Generator Mk. 2");
		ModLoader.addName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 7), "Water Strainer");
		
		ModLoader.addName(PowerConverterCore.jetpackFuellerItem, "Jetpack Fueller");
	}
	
	@Override
	public String getVersion()
	{
		return PowerConverterCore.version;
	}
	
	/** This is to force Power Converters to load after the required mods, IC2 and BC2 + BC2 Energy.
	  * Unfortunately this doesn't work with server-side MLMP.
	  */
	@Override
	public String getPriorities()
	{
		return "after:mod_BuildCraftCore;after:mod_BuildCraftEnergy;after:mod_IC2";
	}	
	
	@Override
	public void modsLoaded()
	{
		PowerConverterCore.afterModsLoaded();
		MinecraftForgeClient.preloadTexture(PowerConverterCore.terrainTexture);
	}
	
	@Override
	public void handleTileEntityPacket(int x, int y, int z, int l, int ai[], float af[], String as[])
	{
		World w = ModLoader.getMinecraftInstance().theWorld;
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityLiquidGenerator)
		{
			((TileEntityLiquidGenerator)te).setStoredLiquid(ai[0]);
			w.markBlocksDirty(x, y, z, x, y, z);
		}
	}
	
	public class ClientProxy implements IPCProxy
	{
		@Override
		public String getConfigPath()
		{
			return Minecraft.getMinecraftDir() + "/config/PowerConverters.cfg";
		}

		@Override
		public void sendPacketToAll(Packet230ModLoader packet)
		{
		}

		@Override
		public boolean isServer()
		{
			return false;
		}

		@Override
		public Packet230ModLoader getTileEntityPacket(TileEntity te, int[] dataInt, float[] dataFloat, String[] dataString)
		{
			return null;
		}

		@Override
		public boolean isClient(World world)
		{
			return world.isRemote;
		}

		@Override
		public void sendTileEntityPacket(TileEntity te)
		{
		}
	}
}
