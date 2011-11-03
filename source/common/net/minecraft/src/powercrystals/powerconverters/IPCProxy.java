package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public interface IPCProxy
{
	public String getConfigPath();
	
	public void sendPacketToAll(Packet230ModLoader packet);
	
	public boolean isClient(World world);
	
	public boolean isServer();
	
	public Packet230ModLoader getTileEntityPacket(TileEntity te, int[] dataInt, float[] dataFloat, String[] dataString);
	
	public void sendTileEntityPacket(TileEntity te);
}
