package net.minecraft.src.powercrystals.powerconverters;

import java.io.File;

import net.minecraft.src.Block;
import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.BuildCraftEnergy;
import net.minecraft.src.BuildCraftFactory;
import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_IC2;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;

public class PowerConverterCore
{
	public static String version = "1.1.0";
	
	public static String terrainTexture = "/PowerConverterSprites/terrain_0.png";
	
	public static Block powerConverterBlock;
	
	public static Property powerConverterBlockId;
	
	public static int engineGeneratorLVSideTexture = 0;
	public static int engineGeneratorTopBottomTexture = 1;
	public static int oilFabricatorSideTexture = 2;
	public static int oilFabricatorTopBottomTexture = 3;
	public static int engineGeneratorMVSideTexture = 4;
	public static int engineGeneratorHVSideTexture = 5;
	public static int energyLinkSideTexture = 6;
	public static int energyLinkSideOffTexture = 7;
	
	public static int bcToICScaleNumerator;
	public static int bcToICScaleDenominator;
	public static int icToBCScaleNumerator;
	public static int icToBCScaleDenominator;
	public static int oilUnitCostInEU;
	
	public static void init(String configPath)
	{
		Configuration c = new Configuration(new File(configPath));
		c.load();
		powerConverterBlockId = c.getOrCreateBlockIdProperty("ID.PowerConverter", 190);
		
		Property bcToICScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Numerator", Configuration.GENERAL_PROPERTY, 5);
		Property bcToICScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Denominator", Configuration.GENERAL_PROPERTY, 2);
		bcToICScaleDenominatorProperty.comment = "This property and Numerator set the ratio for power conversion. By default, going off the value of a piece of coal, one BC MJ is worth 2.5 IC2 EUs.";
		Property icToBCScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Numerator", Configuration.GENERAL_PROPERTY, 2);
		Property icToBCScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Denominator", Configuration.GENERAL_PROPERTY, 5);
		icToBCScaleDenominatorProperty.comment = "This by default is 2/5, the inverse of the BC to IC scale. Note that the Energy Link block has a currently unfixed bug which will add ~10% loss on top of this ratio.";
		Property oilCostEUProperty = c.getOrCreateIntProperty("Scale.OilCostInEU", Configuration.GENERAL_PROPERTY, 625);
		oilCostEUProperty.comment = "One oil bucket is worth 250,000 BC MJ; there are 1000 units per bucket. Using the above ratio of 2.5 EUs per MJ, one 250 MJ unit is worth 625 EUs.";
		c.save();
		
		bcToICScaleNumerator = Integer.parseInt(bcToICScaleNumeratorProperty.value);
		bcToICScaleDenominator = Integer.parseInt(bcToICScaleDenominatorProperty.value);
		icToBCScaleNumerator = Integer.parseInt(icToBCScaleNumeratorProperty.value);
		icToBCScaleDenominator = Integer.parseInt(icToBCScaleDenominatorProperty.value);
		oilUnitCostInEU = Integer.parseInt(oilCostEUProperty.value);
		
		powerConverterBlock = new BlockPowerConverter(Integer.parseInt(powerConverterBlockId.value));
		
		ModLoader.RegisterBlock(powerConverterBlock, ItemPowerConverter.class);
		
		ModLoader.RegisterTileEntity(TileEntityEngineGenerator.class, "EngineGenerator");
		ModLoader.RegisterTileEntity(TileEntityOilFabricator.class, "OilFabricator");
		ModLoader.RegisterTileEntity(TileEntityEnergyLink.class, "EnergyLink");
	}
	
	public static void afterModsLoaded()
	{
		BuildCraftCore.initialize();
		BuildCraftEnergy.initialize();
		BuildCraftFactory.initialize();
		BuildCraftTransport.initialize();
		
		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 0), new Object[]
			{
				"GEG", "RSR", "GDG",
				Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 0),
				Character.valueOf('S'), new ItemStack(mod_IC2.blockElectric, 1, 0),
				Character.valueOf('G'), Item.ingotGold,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('D'), BuildCraftCore.ironGearItem
			}
		);
		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 1), new Object[]
 			{
				"GEG", "RSR", "GDG",
				Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 1),
				Character.valueOf('S'), new ItemStack(mod_IC2.blockElectric, 1, 1),
				Character.valueOf('G'), Item.ingotGold,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('D'), BuildCraftCore.goldGearItem
 			}
 		);
		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 2), new Object[]
 			{
				"GEG", "RSR", "GDG",
				Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 2),
				Character.valueOf('S'), new ItemStack(mod_IC2.blockElectric, 1, 2),
				Character.valueOf('G'), Item.ingotGold,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('D'), BuildCraftCore.diamondGearItem
 			}
 		);
 		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 3), new Object[]
  			{
				"LDL", "RTR", "LAL",
				Character.valueOf('L'), mod_IC2.itemPartAlloy,
				Character.valueOf('D'), BuildCraftCore.diamondGearItem,
				Character.valueOf('R'), Item.redstone,
				Character.valueOf('T'), Block.tnt,
				Character.valueOf('A'), BuildCraftFactory.tankBlock
  			}
  		);
		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 4), new Object[]
  			{
  				"ARA", "CRP", "GRG",
  				Character.valueOf('A'), mod_IC2.itemPartAlloy,
  				Character.valueOf('G'), BuildCraftCore.goldGearItem,
  				Character.valueOf('C'), mod_IC2.itemCable,
  				Character.valueOf('P'), BuildCraftTransport.pipePowerWood,
  				Character.valueOf('R'), Item.redstone
  			}
  		);
	}
}
