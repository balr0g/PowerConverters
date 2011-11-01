package net.minecraft.src.powercrystals.powerconverters;

import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemPowerConverter extends ItemBlock
{
	public ItemPowerConverter(int i)
	{
		super(i);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	public int getPlacedBlockMetadata(int i)
	{
		return i;
	}
	
	public int getMetadata(int i)
	{
		return i;
	}
	
	public int getIconFromDamage(int i)
	{
		return Math.min(i, 4);
	}
	
	public String getItemNameIS(ItemStack itemstack)
	{
		int md = itemstack.getItemDamage();
		if(md == 0) return "engineGeneratorLV";
		if(md == 1) return "engineGeneratorMV";
		if(md == 2) return "engineGeneratorHV";
		if(md == 3) return "oilFabricator";
		if(md == 4) return "energyLink";
		return "energyLink";
	}
}
