package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.powercrystals.powerconverters.IPCProxy;
import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;
import net.minecraft.src.powercrystals.powerconverters.TileEntityGeoMk2;
import net.minecraft.src.powercrystals.powerconverters.TileEntityWaterStrainer;

public class mod_PowerConverters extends BaseModMp
{
	public mod_PowerConverters()
	{
		PowerConverterCore.init(new ClientProxy());
		
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 0), "Engine Generator (LV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 1), "Engine Generator (MV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 2), "Engine Generator (HV)");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 3), "Oil Fabricator");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 4), "Energy Link");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 5), "Lava Fabricator");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 6), "Geothermal Generator Mk. 2");
		ModLoader.AddName(new ItemStack(PowerConverterCore.powerConverterBlock, 1, 7), "Water Strainer");
	}
	
	@Override
	public String Version()
	{
		return PowerConverterCore.version;
	}
	
	@Override
	public void ModsLoaded()
	{
		PowerConverterCore.afterModsLoaded();
		
		MinecraftForgeClient.preloadTexture(PowerConverterCore.terrainTexture);
		
		ModLoaderMp.Init();
	}
	
	@Override
	public void HandleTileEntityPacket(int x, int y, int z, int l, int ai[], float af[], String as[])
	{
		World w = ModLoader.getMinecraftInstance().theWorld;
		TileEntity te = w.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityGeoMk2)
		{
			((TileEntityGeoMk2)te).setStoredLiquid(ai[0]);
			w.markBlocksDirty(x, y, z, x, y, z);
		}
		if(te != null && te instanceof TileEntityWaterStrainer)
		{
			((TileEntityWaterStrainer)te).setWaterDirectly(ai[0]);
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
			return world.multiplayerWorld;
		}

		@Override
		public void sendTileEntityPacket(TileEntity te)
		{
		}
	}
}
