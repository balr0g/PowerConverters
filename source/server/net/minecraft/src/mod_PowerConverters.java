package net.minecraft.src;

import net.minecraft.src.powercrystals.powerconverters.IPCProxy;
import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;

public class mod_PowerConverters extends BaseModMp
{
	private static mod_PowerConverters instance;
	
	@Override
	public String getVersion()
	{
		return PowerConverterCore.version;
	}
	@Override
	public void load()
	{
		instance = this;
		PowerConverterCore.init(new ServerProxy());
	}
	
	/** This is to force Power Converters to load after the required mods, IC2 and BC2 + BC2 Energy.
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
	}
	
	public class ServerProxy implements IPCProxy
	{
		@Override
		public String getConfigPath()
		{
			return "config/PowerConverters.cfg";
		}

		@Override
		public void sendPacketToAll(Packet230ModLoader packet)
		{
			ModLoaderMp.sendPacketToAll(instance, packet);
		}

		@Override
		public boolean isServer()
		{
			return true;
		}
		
		@Override
		public Packet230ModLoader getTileEntityPacket(TileEntity te, int[] dataInt, float[] dataFloat, String[] dataString)
		{
			return (Packet230ModLoader)ModLoaderMp.getTileEntityPacket(instance, te.xCoord, te.yCoord, te.zCoord, 0, dataInt, dataFloat, dataString);
		}

		@Override
		public boolean isClient(World world)
		{
			return false;
		}

		@Override
		public void sendTileEntityPacket(TileEntity te)
		{
			ModLoaderMp.sendTileEntityPacket(te);
		}
	}
}
