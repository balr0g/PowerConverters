package net.minecraft.src.powercrystals.powerconverters;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.forge.ITextureProvider;

public class BlockPowerConverter extends BlockContainer implements ITextureProvider
{
	// 0 - engine gen lv
	// 1 - engine gen mv
	// 2 - engine gen hv
	// 3 - oil fab
	// 4 - energy link
	// 5 - lava fab
	// 6 - geo mk2
	
	public static int[][] textures = new int[6][16];
	
	public BlockPowerConverter(int i)
	{
		super(i, 0, Material.circuits);
		setHardness(1.0F);
		setBlockName("powerConverter");
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j)
	{
		return textures[i][j];
	}
	
    public int getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
    	int meta = iblockaccess.getBlockMetadata(x, y, z);
		TileEntity te = iblockaccess.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityEnergyLink)
		{
			if(((TileEntityEnergyLink)te).isConnected(side))
			{
				return PowerConverterCore.energyLinkSideConnectedTexture;
			}
			else
			{
				return PowerConverterCore.energyLinkSideTexture;
			}
		}
		else if(te != null && te instanceof TileEntityGeoMk2)
		{
			TileEntityGeoMk2 geo = ((TileEntityGeoMk2)te);
			if(side == 0)
			{
				return PowerConverterCore.defaultBottomTexture;
			}
			if(side == 1)
			{
				if(geo.isActive())
				{
					return PowerConverterCore.geoMk2TopTexture_On;
				}
				else
				{
					return PowerConverterCore.geoMk2TopTexture_Off;
				}
			}
			else
			{
				if(geo.isActive() && geo.isConnected(side))
				{
					return PowerConverterCore.geoMk2SideTexture_On_Connected;
				}
				else if(geo.isActive())
				{
					return PowerConverterCore.geoMk2SideTexture_On_Disconnected;
				}
				else if(!geo.isActive() && geo.isConnected(side))
				{
					return PowerConverterCore.geoMk2SideTexture_Off_Connected;
				}
				else
				{
					return PowerConverterCore.geoMk2SideTexture_Off_Disconnected;
				}
			}
		}
		else
		{
			return getBlockTextureFromSideAndMetadata(side, meta);
		}
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null)
		{
			return;
		}
		else if(te instanceof TileEntityEngineGenerator)
		{
			((TileEntityEngineGenerator)te).resetEnergyNetwork();
		}
	}

	@Override
	public TileEntity getBlockEntity(int md)
	{
		if(md == 0) return new TileEntityEngineGenerator(30, 100);
		if(md == 1) return new TileEntityEngineGenerator(120, 1000);
		if(md == 2) return new TileEntityEngineGenerator(510, 10000);
		if(md == 3) return new TileEntityOilFabricator();
		if(md == 4) return new TileEntityEnergyLink();
		if(md == 5) return new TileEntityLavaFabricator();
		if(md == 6) return new TileEntityGeoMk2();
		return getBlockEntity();
	}

	@Override
	public TileEntity getBlockEntity()
	{
		return null;
	}

	@Override
	protected int damageDropped(int i)
	{
		return i;
	}

	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if(meta != 6)
		{
			return;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te == null || !(te instanceof TileEntityGeoMk2) || !((TileEntityGeoMk2)te).isActive())
		{
			return;
		}
		
		float xOffset = random.nextFloat() * (10.0F/16.0F) + (3.0F/16.0F);
		float zOffset = random.nextFloat() * (10.0F/16.0F) + (3.0F/16.0F);
		
		world.spawnParticle("smoke", x + xOffset, y + 1.1, z + zOffset, 0.0D, 0.0D, 0.0D);
		world.spawnParticle("flame", x + xOffset, y + 1, z + zOffset, 0.0D, 0.0D, 0.0D);
	}
	
	@Override
	public String getTextureFile()
	{
		return PowerConverterCore.terrainTexture;
	}
}
