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
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;

public class PowerConverterCore
{
	public static String version = "1.8.1R1.2.0";
	
	public static String terrainTexture = "/PowerConverterSprites/terrain_0.png";
	public static String itemTexture = "/PowerConverterSprites/items_0.png";
	
	public static Block powerConverterBlock;
	
	public static Item jetpackFuellerItem;
	
	public static Property powerConverterBlockId;
	public static Property jetpackFuellerItemId;
	
	public static int textureOffsetEngineGeneratorLV = 6;
	public static int textureOffsetEngineGeneratorMV = 12;
	public static int textureOffsetEngineGeneratorHV = 18;
	public static int textureOffsetOilFabricator = 24;
	public static int textureOffsetEnergyLinkDisconnected = 30;
	public static int textureOffsetEnergyLinkConnected = 36;
	public static int textureOffsetLavaFabricator = 42;
	public static int textureOffsetGeomk2OffDisconnected = 48;
	public static int textureOffsetGeomk2OffConnected = 54;
	public static int textureOffsetGeomk2OnDisconnected = 60;
	public static int textureOffsetGeomk2OnConnected = 66;
	public static int textureOffsetWaterStrainerOffDisconnected = 72;
	public static int textureOffsetWaterStrainerOffConnected = 78;
	public static int textureOffsetWaterStrainerOnDisconnected = 84;
	public static int textureOffsetWaterStrainerOnConnected = 90;
	
	public static int bcToICScaleNumerator;
	public static int bcToICScaleDenominator;
	public static int icToBCScaleNumerator;
	public static int icToBCScaleDenominator;
	public static int oilUnitCostInEU;
	public static int lavaUnitCostInEU;
	public static int euProducedPerLavaUnit;
	public static int euProducedPerWaterUnit;
	public static int waterConsumedPerOutput;
	public static int jetpackFuelRefilledPerFuelUnit;
	
	public static boolean enableFuelConversion;
	public static boolean enableJetpackFueller;
	
	public static IPCProxy proxy;
	
	public static void init(IPCProxy proxyParam)
	{
		proxy = proxyParam;
		
		Configuration c = new Configuration(new File(proxy.getConfigPath()));
		c.load();
		
		powerConverterBlockId = c.getOrCreateBlockIdProperty("ID.PowerConverter", 190);
		jetpackFuellerItemId = c.getOrCreateIntProperty("ID.JetpackFueller", Configuration.ITEM_PROPERTY, 17900);
		
		Property bcToICScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Numerator", Configuration.GENERAL_PROPERTY, 5);
		Property bcToICScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Denominator", Configuration.GENERAL_PROPERTY, 2);
		bcToICScaleDenominatorProperty.comment = "This property and Numerator set the ratio for power conversion. By default, going off the value of a piece of coal, one BC MJ is worth 2.5 IC2 EUs.";
		
		Property icToBCScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Numerator", Configuration.GENERAL_PROPERTY, 2);
		Property icToBCScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Denominator", Configuration.GENERAL_PROPERTY, 5);
		icToBCScaleDenominatorProperty.comment = "This by default is 2/5, the inverse of the BC to IC scale. Note that the Energy Link block has a currently unfixed bug which will add ~10% loss on top of this ratio.";
		
		Property oilCostEUProperty = c.getOrCreateIntProperty("Scale.OilCostInEU", Configuration.GENERAL_PROPERTY, 50);
		oilCostEUProperty.comment = "One oil bucket is worth 20,000 BC MJ; there are 1000 units per bucket. Using the above ratio of 2.5 EUs per MJ, one 20 MJ unit is worth 50 EUs.";
		Property lavaCostEUProperty = c.getOrCreateIntProperty("Scale.LavaCostInEU", Configuration.GENERAL_PROPERTY, 50);
		lavaCostEUProperty.comment = "One lava bucket is worth 20,000 BC MJ; there are 1000 units per bucket. Using the above ratio of 2.5 EUs per MJ, one 20 MJ unit is worth 50 EUs. Note that lava is worth less (20EU per unit) in IC2 than in BC.";
		Property euProducedPerLavaUnitProperty = c.getOrCreateIntProperty("Scale.EUGeneratedPerLavaUnit", Configuration.GENERAL_PROPERTY, 50);
		euProducedPerLavaUnitProperty.comment = "See comments on the lava unit cost property. This number should probably match that one, but this is for how much power the geo mk2 produces.";
		Property waterConsumedPerOutputProperty = c.getOrCreateIntProperty("Scale.WaterConsumedPerTick", Configuration.GENERAL_PROPERTY, 2);
		waterConsumedPerOutputProperty.comment = "Combines with Scale.EUGeneratedPerWaterOutput for the Water Strainer. This determines how much water is used per tick, to enable greater water consumption as you cannot go lower than 1 EU per water unit without going to 0. Note that the water strainer will only consume a constant amount of water per tick, so this will also throttle its output.";
		Property euProducedPerWaterOutputProperty = c.getOrCreateIntProperty("Scale.EUGeneratedPerWaterOutput", Configuration.GENERAL_PROPERTY, 1);
		euProducedPerWaterOutputProperty.comment = "IC2's water generator produces 1000 EU per water bucket, or 1 EU per water unit. BC has no equivalent.";
		Property jetpackFuelRefilledPerFuelUnitProperty = c.getOrCreateIntProperty("Scale.JetpackFuelFilledPerFuelUnit", Configuration.GENERAL_PROPERTY, 468);
		jetpackFuelRefilledPerFuelUnitProperty.comment = "A jetpack is fully fuelled by 6 coalfuel cells, which are 4,000 EUs each, or 24000 EUs total. The Jetpack has 18,000 fuel units. Each unit is worth 1.33333... EUs. Each unit of fuel is worth 625 EUs. Thus, each unit of BC fuel is worth 468.75 (ish) jetpack fuel units, or 468 rounded down.";
		
		Property enableJetpackFuellingItemProperty = c.getOrCreateBooleanProperty("Enable.JetpackFueller", Configuration.GENERAL_PROPERTY, true);
		enableJetpackFuellingItemProperty.comment = "If false, the jetpack fueller item will be removed entirely.";
		Property enableFuelConversionCraftingProperty = c.getOrCreateBooleanProperty("Enable.FuelConversionCrafting", Configuration.GENERAL_PROPERTY, true);
		enableFuelConversionCraftingProperty.comment = "If true, you can craft a BC fuel bucket + IC empty fuel can into a filled IC fuel can. The reverse is not provided, as it would be a massive gain of energy.";
		
		c.save();
		
		bcToICScaleNumerator = Integer.parseInt(bcToICScaleNumeratorProperty.value);
		bcToICScaleDenominator = Integer.parseInt(bcToICScaleDenominatorProperty.value);
		icToBCScaleNumerator = Integer.parseInt(icToBCScaleNumeratorProperty.value);
		icToBCScaleDenominator = Integer.parseInt(icToBCScaleDenominatorProperty.value);
		oilUnitCostInEU = Integer.parseInt(oilCostEUProperty.value);
		lavaUnitCostInEU = Integer.parseInt(lavaCostEUProperty.value);
		euProducedPerLavaUnit = Integer.parseInt(euProducedPerLavaUnitProperty.value);
		euProducedPerWaterUnit = Integer.parseInt(euProducedPerWaterOutputProperty.value);
		waterConsumedPerOutput = Integer.parseInt(waterConsumedPerOutputProperty.value);
		jetpackFuelRefilledPerFuelUnit = Integer.parseInt(jetpackFuelRefilledPerFuelUnitProperty.value);
		enableFuelConversion = Boolean.parseBoolean(enableFuelConversionCraftingProperty.value);
		enableJetpackFueller = Boolean.parseBoolean(enableJetpackFuellingItemProperty.value);
		
		powerConverterBlock = new BlockPowerConverter(Integer.parseInt(powerConverterBlockId.value));
		
		ModLoader.RegisterBlock(powerConverterBlock, ItemPowerConverter.class);
		
		if(enableJetpackFueller)
		{
			jetpackFuellerItem = new ItemJetpackFueller(Integer.parseInt(jetpackFuellerItemId.value));
		}
		
		ModLoader.RegisterTileEntity(TileEntityEngineGenerator.class, "EngineGenerator");
		ModLoader.RegisterTileEntity(TileEntityOilFabricator.class, "OilFabricator");
		ModLoader.RegisterTileEntity(TileEntityEnergyLink.class, "EnergyLink");
		ModLoader.RegisterTileEntity(TileEntityLavaFabricator.class, "LavaFabricator");
		ModLoader.RegisterTileEntity(TileEntityGeoMk2.class, "GeothermalMk2");
		ModLoader.RegisterTileEntity(TileEntityWaterStrainer.class, "WaterStrainer");
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
				"LDL", "ATA", "LML",
				Character.valueOf('L'), mod_IC2.itemPartAlloy,
				Character.valueOf('D'), BuildCraftCore.diamondGearItem,
				Character.valueOf('T'), Block.tnt,
				Character.valueOf('A'), BuildCraftFactory.tankBlock,
				Character.valueOf('M'), new ItemStack(mod_IC2.blockMachine, 1, 14),
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
 		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 5), new Object[]
  			{
				"LDL", "ATA", "LML",
				Character.valueOf('L'), mod_IC2.itemPartAlloy,
				Character.valueOf('D'), BuildCraftCore.goldGearItem,
				Character.valueOf('T'), Block.stoneOvenIdle,
				Character.valueOf('A'), BuildCraftFactory.tankBlock,
				Character.valueOf('M'), new ItemStack(mod_IC2.blockMachine, 1, 14),
  			}
  		);
 		ModLoader.AddRecipe(new ItemStack(powerConverterBlock, 1, 7), new Object[]
  			{
				"TWP",
				Character.valueOf('T'), BuildCraftFactory.tankBlock,
				Character.valueOf('W'), new ItemStack(mod_IC2.blockGenerator, 1, 2),
				Character.valueOf('P'), BuildCraftTransport.pipeLiquidsIron
  			}
  		);
 		ModLoader.AddShapelessRecipe(new ItemStack(powerConverterBlock, 1, 6), new Object[]
			{
				new ItemStack(mod_IC2.blockGenerator, 1, 1),
				BuildCraftFactory.tankBlock
			}
		);
 		
 		if(enableJetpackFueller)
 		{
 			ModLoader.AddRecipe(new ItemStack(jetpackFuellerItem), new Object[]
 				{
 					"WRS",
 					Character.valueOf('W'), BuildCraftTransport.pipeLiquidsWood,
 					Character.valueOf('R'), mod_IC2.itemRubber,
 					Character.valueOf('S'), Item.stick
 				}
 			);
 		}
 		if(enableFuelConversion)
 		{
 	 		ModLoader.AddShapelessRecipe(new ItemStack(mod_IC2.itemFuelCan), new Object[]
    			{
    				new ItemStack(mod_IC2.itemFuelCanEmpty),
    				BuildCraftEnergy.bucketFuel
    			}
    		);
 		}
	}
	
	public static Orientations getOrientationFromSide(int side)
	{
		if(side == 0) return Orientations.YNeg;
		if(side == 1) return Orientations.YPos;
		if(side == 2) return Orientations.ZNeg;
		if(side == 3) return Orientations.ZPos;
		if(side == 4) return Orientations.XNeg;
		if(side == 5) return Orientations.XPos;
		return Orientations.Unknown;
	}
}
