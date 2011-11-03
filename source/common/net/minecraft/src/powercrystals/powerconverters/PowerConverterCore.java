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
	public static String version = "1.8.1R1.1.1";
	
	public static String terrainTexture = "/PowerConverterSprites/terrain_0.png";
	
	public static Block powerConverterBlock;
	
	public static Property powerConverterBlockId;
	
	public static int defaultTopTexture = 0;
	public static int defaultBottomTexture = 1;
	public static int engineGeneratorLVSideTexture = 2;
	public static int engineGeneratorMVSideTexture = 3;
	public static int engineGeneratorHVSideTexture = 4;
	public static int oilFabricatorSideTexture = 5;
	public static int energyLinkSideTexture = 6;
	public static int energyLinkSideConnectedTexture = 7;
	public static int lavaFabricatorSideTexture = 8;
	public static int geoMk2SideTexture_Off_Disconnected = 9;
	public static int geoMk2SideTexture_Off_Connected = 10;
	public static int geoMk2SideTexture_On_Disconnected = 11;
	public static int geoMk2SideTexture_On_Connected = 12;
	public static int geoMk2TopTexture_Off = 13;
	public static int geoMk2TopTexture_On = 14;
	
	public static int bcToICScaleNumerator;
	public static int bcToICScaleDenominator;
	public static int icToBCScaleNumerator;
	public static int icToBCScaleDenominator;
	public static int oilUnitCostInEU;
	public static int lavaUnitCostInEU;
	public static int euProducedPerLavaUnit;
	
	public static IPCProxy proxy;
	
	public static void init(IPCProxy proxyParam)
	{
		proxy = proxyParam;
		setupTextures();
		
		Configuration c = new Configuration(new File(proxy.getConfigPath()));
		c.load();
		powerConverterBlockId = c.getOrCreateBlockIdProperty("ID.PowerConverter", 190);
		
		Property bcToICScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Numerator", Configuration.GENERAL_PROPERTY, 5);
		Property bcToICScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.BCtoIC.Denominator", Configuration.GENERAL_PROPERTY, 2);
		bcToICScaleDenominatorProperty.comment = "This property and Numerator set the ratio for power conversion. By default, going off the value of a piece of coal, one BC MJ is worth 2.5 IC2 EUs.";
		
		Property icToBCScaleNumeratorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Numerator", Configuration.GENERAL_PROPERTY, 2);
		Property icToBCScaleDenominatorProperty = c.getOrCreateIntProperty("Scale.ICtoBC.Denominator", Configuration.GENERAL_PROPERTY, 5);
		icToBCScaleDenominatorProperty.comment = "This by default is 2/5, the inverse of the BC to IC scale. Note that the Energy Link block has a currently unfixed bug which will add ~10% loss on top of this ratio.";
		
		Property oilCostEUProperty = c.getOrCreateIntProperty("Scale.OilCostInEU", Configuration.GENERAL_PROPERTY, 50);
		oilCostEUProperty.comment = "One oil bucket is worth 20,000 BC MJ; there are 1000 units per bucket. Using the above ratio of 2.5 EUs per MJ, one 20 MJ unit is worth 50 EUs.";
		Property lavaCostEUProperty = c.getOrCreateIntProperty("Scale.LavaCostInEU", Configuration.GENERAL_PROPERTY, 20);
		lavaCostEUProperty.comment = "One lava bucket is worth 2000 BC MJ; there are 1000 units per bucket. However, BC's lava is worth way less than it should compared to IC's - an IC lava bucket is 20k EU (20 EU per unit), but a BC lava bucket is only 5k EU (5 EU per unit). The number is thus set for IC lava, as it's more expensive.";
		Property euProducedPerLavaUnitProperty = c.getOrCreateIntProperty("Scale.EUGeneratedPerLavaUnit", Configuration.GENERAL_PROPERTY, 20);
		euProducedPerLavaUnitProperty.comment = "See comments on the lava unit cost property. This number should probably match that one, but this is for how much power the geo mk2 produces.";
		
		c.save();
		
		bcToICScaleNumerator = Integer.parseInt(bcToICScaleNumeratorProperty.value);
		bcToICScaleDenominator = Integer.parseInt(bcToICScaleDenominatorProperty.value);
		icToBCScaleNumerator = Integer.parseInt(icToBCScaleNumeratorProperty.value);
		icToBCScaleDenominator = Integer.parseInt(icToBCScaleDenominatorProperty.value);
		oilUnitCostInEU = Integer.parseInt(oilCostEUProperty.value);
		lavaUnitCostInEU = Integer.parseInt(lavaCostEUProperty.value);
		euProducedPerLavaUnit = Integer.parseInt(euProducedPerLavaUnitProperty.value);
		
		powerConverterBlock = new BlockPowerConverter(Integer.parseInt(powerConverterBlockId.value));
		
		ModLoader.RegisterBlock(powerConverterBlock, ItemPowerConverter.class);
		
		ModLoader.RegisterTileEntity(TileEntityEngineGenerator.class, "EngineGenerator");
		ModLoader.RegisterTileEntity(TileEntityOilFabricator.class, "OilFabricator");
		ModLoader.RegisterTileEntity(TileEntityEnergyLink.class, "EnergyLink");
		ModLoader.RegisterTileEntity(TileEntityLavaFabricator.class, "LavaFabricator");
		ModLoader.RegisterTileEntity(TileEntityGeoMk2.class, "GeothermalMk2");
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
 		ModLoader.AddShapelessRecipe(new ItemStack(powerConverterBlock, 1, 6), new Object[]
			{
				new ItemStack(mod_IC2.blockGenerator, 1, 1),
				BuildCraftFactory.tankBlock
			}
		);
	}
	
	private static void setupTextures()
	{
		BlockPowerConverter.textures[0][0] = defaultBottomTexture;
		BlockPowerConverter.textures[1][0] = defaultTopTexture;
		BlockPowerConverter.textures[2][0] = engineGeneratorLVSideTexture;
		BlockPowerConverter.textures[3][0] = engineGeneratorLVSideTexture;
		BlockPowerConverter.textures[4][0] = engineGeneratorLVSideTexture;
		BlockPowerConverter.textures[5][0] = engineGeneratorLVSideTexture;

		BlockPowerConverter.textures[0][1] = defaultBottomTexture;
		BlockPowerConverter.textures[1][1] = defaultTopTexture;
		BlockPowerConverter.textures[2][1] = engineGeneratorMVSideTexture;
		BlockPowerConverter.textures[3][1] = engineGeneratorMVSideTexture;
		BlockPowerConverter.textures[4][1] = engineGeneratorMVSideTexture;
		BlockPowerConverter.textures[5][1] = engineGeneratorMVSideTexture;

		BlockPowerConverter.textures[0][2] = defaultBottomTexture;
		BlockPowerConverter.textures[1][2] = defaultTopTexture;
		BlockPowerConverter.textures[2][2] = engineGeneratorHVSideTexture;
		BlockPowerConverter.textures[3][2] = engineGeneratorHVSideTexture;
		BlockPowerConverter.textures[4][2] = engineGeneratorHVSideTexture;
		BlockPowerConverter.textures[5][2] = engineGeneratorHVSideTexture;

		BlockPowerConverter.textures[0][3] = defaultBottomTexture;
		BlockPowerConverter.textures[1][3] = defaultTopTexture;
		BlockPowerConverter.textures[2][3] = oilFabricatorSideTexture;
		BlockPowerConverter.textures[3][3] = oilFabricatorSideTexture;
		BlockPowerConverter.textures[4][3] = oilFabricatorSideTexture;
		BlockPowerConverter.textures[5][3] = oilFabricatorSideTexture;

		BlockPowerConverter.textures[0][4] = energyLinkSideTexture;
		BlockPowerConverter.textures[1][4] = energyLinkSideTexture;
		BlockPowerConverter.textures[2][4] = energyLinkSideTexture;
		BlockPowerConverter.textures[3][4] = energyLinkSideTexture;
		BlockPowerConverter.textures[4][4] = energyLinkSideTexture;
		BlockPowerConverter.textures[5][4] = energyLinkSideTexture;

		BlockPowerConverter.textures[0][5] = defaultBottomTexture;
		BlockPowerConverter.textures[1][5] = defaultTopTexture;
		BlockPowerConverter.textures[2][5] = lavaFabricatorSideTexture;
		BlockPowerConverter.textures[3][5] = lavaFabricatorSideTexture;
		BlockPowerConverter.textures[4][5] = lavaFabricatorSideTexture;
		BlockPowerConverter.textures[5][5] = lavaFabricatorSideTexture;

		BlockPowerConverter.textures[0][6] = defaultBottomTexture;
		BlockPowerConverter.textures[1][6] = geoMk2TopTexture_Off;
		BlockPowerConverter.textures[2][6] = geoMk2SideTexture_Off_Disconnected;
		BlockPowerConverter.textures[3][6] = geoMk2SideTexture_Off_Disconnected;
		BlockPowerConverter.textures[4][6] = geoMk2SideTexture_Off_Disconnected;
		BlockPowerConverter.textures[5][6] = geoMk2SideTexture_Off_Disconnected;
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
