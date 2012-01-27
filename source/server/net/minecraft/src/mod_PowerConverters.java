package net.minecraft.src;

import net.minecraft.src.powercrystals.powerconverters.IPCProxy;
import net.minecraft.src.powercrystals.powerconverters.PowerConverterCore;

public class mod_PowerConverters extends BaseModMp
{
	private static mod_PowerConverters instance;
	
	public mod_PowerConverters()
	{
		instance = this;
		PowerConverterCore.init(new ServerProxy());
	}
	
	@Override
	public String getVersion()
	{
		return PowerConverterCore.version;
	}
	@Override
	public void load()
	{
	}

	
	@Override
	public void ModsLoaded()
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
			ModLoaderMp.SendPacketToAll(instance, packet);
		}

		@Override
		public boolean isServer()
		{
			return true;
		}
		
		@Override
		public Packet230ModLoader getTileEntityPacket(TileEntity te, int[] dataInt, float[] dataFloat, String[] dataString)
		{
			return (Packet230ModLoader)ModLoaderMp.GetTileEntityPacket(instance, te.xCoord, te.yCoord, te.zCoord, 0, dataInt, dataFloat, dataString);
		}

		@Override
		public boolean isClient(World world)
		{
			return false;
		}

		@Override
		public void sendTileEntityPacket(TileEntity te)
		{
			ModLoaderMp.SendTileEntityPacket(te);
		}
	}
}
