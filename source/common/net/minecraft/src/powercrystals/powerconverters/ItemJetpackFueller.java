package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.BuildCraftEnergy;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2;
import net.minecraft.src.buildcraft.factory.TileTank;
import net.minecraft.src.forge.ITextureProvider;

public class ItemJetpackFueller extends Item// implements ITextureProvider
{
	public ItemJetpackFueller(int i)
	{
		super(i);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileTank)
		{
			TileTank tank = ((TileTank)te);
			if(tank.liquidId == BuildCraftEnergy.fuel.shiftedIndex)
			{
				for(int i = 0; i < entityplayer.inventory.getSizeInventory(); i++)
				{
					ItemStack s = entityplayer.inventory.getStackInSlot(i);
					if(s != null && s.itemID == mod_IC2.itemArmorJetpack.shiftedIndex)
					{
						int fuelToUse = (int)Math.ceil((double)s.getMaxDamage() - (double)s.getItemDamage());
						int fuelUsed = tank.empty(fuelToUse, true);
						s.damageItem(-(fuelUsed * PowerConverterCore.jetpackFuelRefilledPerFuelUnit), entityplayer);
						return false;
					}
				}
			}
		}
		return false;
	}
	
	//@Override
	public String getTextureFile()
	{
		return null;
	}

}
