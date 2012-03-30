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
import net.minecraft.src.ic2.api.Items;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;

public class PowerConverterCore
{
	public static String version = "1.2.3R1.3.2";
	
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
	public static int fuelCanDamageValue;
	
	public static int euPerSecondLava;
	public static int euPerSecondWater;
	
	public static boolean enableFuelConversion;
	
	public static boolean enableEngineGenerator;
	public static boolean enableEnergyLink;
	public static boolean enableLavaFab;
	public static boolean enableOilFab;
	public static boolean enableGeoMk2;
	public static boolean enableWaterStrainer;
	
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
		
		Property euPerSecondLavaProperty = c.getOrCreateIntProperty("Rate.GeoMk2EUPerTick", Configuration.GENERAL_PROPERTY, 20);
		euPerSecondLavaProperty.comment = "The EU/t output of the Geothermal Mk. 2.";
		Property euPerSecondWaterProperty = c.getOrCreateIntProperty("Rate.WaterStrainerEUPerTick", Configuration.GENERAL_PROPERTY, 2);
		euPerSecondWaterProperty.comment = "The EU/t output of the Water Strainer.";
		
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
		Property enableEngineGeneratorProperty = c.getOrCreateBooleanProperty("Enable.EngineGenerator", Configuration.GENERAL_PROPERTY, true);
		Property enableEnergyLinkProperty = c.getOrCreateBooleanProperty("Enable.EnergyLink", Configuration.GENERAL_PROPERTY, true);
		Property enableLavaFabProperty = c.getOrCreateBooleanProperty("Enable.LavaFabricator", Configuration.GENERAL_PROPERTY, true);
		Property enableOilFabProperty = c.getOrCreateBooleanProperty("Enable.OilFabricator", Configuration.GENERAL_PROPERTY, true);
		Property enableGeoMk2Property = c.getOrCreateBooleanProperty("Enable.GeothermalMk2", Configuration.GENERAL_PROPERTY, false);
		Property enableWaterStrainerProperty = c.getOrCreateBooleanProperty("Enable.WaterStrainer", Configuration.GENERAL_PROPERTY, true);
		
		Property enableFuelConversionCraftingProperty = c.getOrCreateBooleanProperty("Enable.FuelConversionCrafting", Configuration.GENERAL_PROPERTY, true);
		enableFuelConversionCraftingProperty.comment = "If true, you can craft a BC fuel bucket + IC empty fuel can into a filled IC fuel can. The reverse is not provided, as it would be a massive gain of energy.";
		Property fuelConversionValueProperty = c.getOrCreateIntProperty("Scale.CraftedFuelCanValue", Configuration.GENERAL_PROPERTY, 16000);
		fuelConversionValueProperty.comment = "The value of a fuel can crafted from BC fuel buckets. There are 5 EUs per each unit of this setting. Note that as this is stored in the can's damage value, the maximum setting is 32767; settings above 16000 seem to not function correctly with IC2.";
		
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
		fuelCanDamageValue = Integer.parseInt(fuelConversionValueProperty.value);
		
		euPerSecondLava = Integer.parseInt(euPerSecondLavaProperty.value);
		euPerSecondWater = Integer.parseInt(euPerSecondWaterProperty.value);
		
		enableFuelConversion = Boolean.parseBoolean(enableFuelConversionCraftingProperty.value);
		
		enableJetpackFueller = Boolean.parseBoolean(enableJetpackFuellingItemProperty.value);
		enableEngineGenerator = Boolean.parseBoolean(enableEngineGeneratorProperty.value);
		enableEnergyLink = Boolean.parseBoolean(enableEnergyLinkProperty.value);
		enableLavaFab = Boolean.parseBoolean(enableLavaFabProperty.value);
		enableOilFab = Boolean.parseBoolean(enableOilFabProperty.value);
		enableGeoMk2 = Boolean.parseBoolean(enableGeoMk2Property.value);
		enableWaterStrainer = Boolean.parseBoolean(enableWaterStrainerProperty.value);
		
		powerConverterBlock = new BlockPowerConverter(Integer.parseInt(powerConverterBlockId.value));
		
		ModLoader.registerBlock(powerConverterBlock, ItemPowerConverter.class);
		
		jetpackFuellerItem = new ItemJetpackFueller(Integer.parseInt(jetpackFuellerItemId.value));
		
		ModLoader.registerTileEntity(TileEntityEngineGenerator.class, "EngineGenerator");
		ModLoader.registerTileEntity(TileEntityOilFabricator.class, "OilFabricator");
		ModLoader.registerTileEntity(TileEntityEnergyLink.class, "EnergyLink");
		ModLoader.registerTileEntity(TileEntityLavaFabricator.class, "LavaFabricator");
		ModLoader.registerTileEntity(TileEntityGeoMk2.class, "GeothermalMk2");
		ModLoader.registerTileEntity(TileEntityWaterStrainer.class, "WaterStrainer");
	}
	
	public static void afterModsLoaded()
	{
		BuildCraftCore.initialize();
		BuildCraftEnergy.initialize();
		BuildCraftFactory.initialize();
		BuildCraftTransport.initialize();
		
		// Engine generators
		if(enableEngineGenerator)
		{
			// LV engine generator
			ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 0), new Object[]
				{
					"GEG", "RSR", "GDG",
					Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 0),
					Character.valueOf('S'), Items.getItem("lvTransformer"),
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('R'), Item.redstone,
					Character.valueOf('D'), BuildCraftCore.ironGearItem
				}
			);
			
			// MV engine generator
			ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 1), new Object[]
	 			{
					"GEG", "RSR", "GDG",
					Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 1),
					Character.valueOf('S'), Items.getItem("mvTransformer"),
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('R'), Item.redstone,
					Character.valueOf('D'), BuildCraftCore.goldGearItem
	 			}
	 		);
	 		
	 		// HV engine generator
			ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 2), new Object[]
	 			{
					"GEG", "RSR", "GDG",
					Character.valueOf('E'), new ItemStack(BuildCraftEnergy.engineBlock, 1, 2),
					Character.valueOf('S'), Items.getItem("hvTransformer"),
					Character.valueOf('G'), Item.ingotGold,
					Character.valueOf('R'), Item.redstone,
					Character.valueOf('D'), BuildCraftCore.diamondGearItem
	 			}
	 		);
		}
		
		// Oil fabricator
		if(enableOilFab)
		{
	 		ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 3), new Object[]
	  			{
					"LDL", "ATA", "LML",
					Character.valueOf('L'), Items.getItem("advancedAlloy"),
					Character.valueOf('D'), BuildCraftCore.diamondGearItem,
					Character.valueOf('T'), Block.tnt,
					Character.valueOf('A'), BuildCraftFactory.tankBlock,
					Character.valueOf('M'), Items.getItem("massFabricator"),
	  			}
	  		);
		}
		
		// Energy link
		if(enableEnergyLink)
		{
			ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 4), new Object[]
	  			{
	  				"ARA", "CRP", "GRG",
	  				Character.valueOf('A'), Items.getItem("advancedAlloy"),
	  				Character.valueOf('G'), BuildCraftCore.goldGearItem,
	  				Character.valueOf('C'), Items.getItem("insulatedCopperCableItem"),
	  				Character.valueOf('P'), BuildCraftTransport.pipePowerWood,
	  				Character.valueOf('R'), Item.redstone
	  			}
	  		);
		}
		
		// Lava fabricator
		if(enableLavaFab)
		{
	 		ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 5), new Object[]
	  			{
					"LDL", "ATA", "LML",
					Character.valueOf('L'), Items.getItem("advancedAlloy"),
					Character.valueOf('D'), BuildCraftCore.goldGearItem,
					Character.valueOf('T'), Block.stoneOvenIdle,
					Character.valueOf('A'), BuildCraftFactory.tankBlock,
					Character.valueOf('M'), Items.getItem("massFabricator"),
	  			}
	  		);
		}
		
		// Water strainer
		if(enableWaterStrainer)
		{
	 		ModLoader.addRecipe(new ItemStack(powerConverterBlock, 1, 7), new Object[]
	  			{
					"TWP",
					Character.valueOf('T'), BuildCraftFactory.tankBlock,
					Character.valueOf('W'), Items.getItem("waterMill"),
					Character.valueOf('P'), BuildCraftTransport.pipeLiquidsIron
	  			}
	  		);
		}
		// Geothermal MK2 -- ALREADY IN IC2 1.64
		if(enableGeoMk2)
		{
	 		ModLoader.addShapelessRecipe(new ItemStack(powerConverterBlock, 1, 6), new Object[]
				{
					Items.getItem("geothermalGenerator"),
					BuildCraftFactory.tankBlock
				}
			);
		}
 		
 		if(enableJetpackFueller)
 		{
 			ModLoader.addRecipe(new ItemStack(jetpackFuellerItem), new Object[]
 				{
 					"WRS",
 					Character.valueOf('W'), BuildCraftTransport.pipeLiquidsWood,
 					Character.valueOf('R'), Items.getItem("rubber"),
 					Character.valueOf('S'), Item.stick
 				}
 			);
 		}
 		if(enableFuelConversion)
 		{
	 		ItemStack newFuelCan = Items.getItem("filledFuelCan").copy();
	 		newFuelCan.setItemDamage(fuelCanDamageValue);
 	 		ModLoader.addShapelessRecipe(newFuelCan, new Object[]
    			{
    				Items.getItem("fuelCan"),
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
