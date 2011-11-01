package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.BlockContainer;
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
	
	public BlockPowerConverter(int i)
	{
		super(i, 0, Material.circuits);
		setHardness(1.0F);
		setBlockName("powerConverter");
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j)
	{
		if(i == 0 || i == 1)
		{
			return PowerConverterCore.engineGeneratorTopBottomTexture;
		}
		else if(j == 0)
		{
			return PowerConverterCore.engineGeneratorLVSideTexture;
		}
		else if(j == 1)
		{
			return PowerConverterCore.engineGeneratorMVSideTexture;
		}
		else if(j == 2)
		{
			return PowerConverterCore.engineGeneratorHVSideTexture;
		}
		else if(j == 3)
		{
			return PowerConverterCore.oilFabricatorSideTexture;
		}
		else if(j == 4)
		{
			return PowerConverterCore.energyLinkSideTexture;
		}
		return blockIndexInTexture;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityEngineGenerator)
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
	
	@Override
	public String getTextureFile()
	{
		return PowerConverterCore.terrainTexture;
	}
}
