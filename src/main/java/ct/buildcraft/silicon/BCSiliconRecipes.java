/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package ct.buildcraft.silicon;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import buildcraft.lib.recipe.RecipeBuilderShaped;
import ct.buildcraft.api.core.BCLog;
import ct.buildcraft.api.enums.EnumEngineType;
import ct.buildcraft.api.enums.EnumRedstoneChipset;
import ct.buildcraft.api.mj.MjAPI;
import ct.buildcraft.api.recipes.IngredientStack;
import ct.buildcraft.core.BCCoreBlocks;
import ct.buildcraft.core.BCCoreConfig;
import ct.buildcraft.core.BCCoreItems;
import ct.buildcraft.lib.misc.ColourUtil;
import ct.buildcraft.lib.recipe.AssemblyRecipe;
import ct.buildcraft.lib.recipe.AssemblyRecipeBasic;
import ct.buildcraft.lib.recipe.AssemblyRecipeRegistry;
import ct.buildcraft.lib.recipe.IngredientNBTBC;
import ct.buildcraft.silicon.gate.EnumGateLogic;
import ct.buildcraft.silicon.gate.EnumGateMaterial;
import ct.buildcraft.silicon.gate.EnumGateModifier;
import ct.buildcraft.silicon.gate.GateVariant;
import ct.buildcraft.silicon.recipe.FacadeAssemblyRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = BCSilicon.MODID)
public class BCSiliconRecipes {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @SubscribeEvent
    public static void registerRecipes(RegisterEvent.Register<Recipe> event) {
/*        if (BCSiliconItems.plugGate != null) {
            // You can craft some of the basic gate types in a normal crafting table
            RecipeBuilderShaped builder = new RecipeBuilderShaped();
            builder.add(" m ");
            builder.add("mrm");
            builder.add(" b ");
            builder.map('r', "dustRedstone");
            builder.map('b', BCItems.Transport.PLUG_BLOCKER, Blocks.COBBLESTONE);

            // Base craftable types

            builder.map('m', Items.BRICK);
            makeGateRecipe(builder, EnumGateMaterial.CLAY_BRICK, EnumGateModifier.NO_MODIFIER);

            builder.map('m', "ingotIron");
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.NO_MODIFIER);

            builder.map('m', Items.NETHERBRICK);
            makeGateRecipe(builder, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.NO_MODIFIER);

            // Iron modifier addition
            GateVariant variant =
                new GateVariant(EnumGateLogic.AND, EnumGateMaterial.IRON, EnumGateModifier.NO_MODIFIER);
            ItemStack ironGateBase = BCSiliconItems.plugGate.getStack(variant);
            builder = new RecipeBuilderShaped();
            builder.add(" m ");
            builder.add("mgm");
            builder.add(" m ");
            builder.map('g', ironGateBase);

            builder.map('m', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.LAPIS);

            builder.map('m', Items.QUARTZ);
            makeGateRecipe(builder, EnumGateMaterial.IRON, EnumGateModifier.QUARTZ);

            // And Gate <-> Or Gate (shapeless)
            // TODO: Create a recipe class for this instead!
            for (EnumGateMaterial material : EnumGateMaterial.VALUES) {
                if (material == EnumGateMaterial.CLAY_BRICK) {
                    continue;
                }
                for (EnumGateModifier modifier : EnumGateModifier.VALUES) {
                    GateVariant varAnd = new GateVariant(EnumGateLogic.AND, material, modifier);
                    ItemStack resultAnd = BCSiliconItems.plugGate.getStack(varAnd);

                    GateVariant varOr = new GateVariant(EnumGateLogic.OR, material, modifier);
                    ItemStack resultOr = BCSiliconItems.plugGate.getStack(varOr);

                    String regNamePrefix = resultOr.getItem().getRegistryName() + "_" + modifier + "_" + material;
                    ForgeRegistries.RECIPES.register(new ShapedOreRecipe(resultOr.getItem().getRegistryName(),
                        resultAnd, "i", 'i', new IngredientNBTBC(resultOr)).setRegistryName(regNamePrefix + "_or"));
                    ForgeRegistries.RECIPES.register(new ShapedOreRecipe(resultAnd.getItem().getRegistryName(),
                        resultOr, "i", 'i', new IngredientNBTBC(resultAnd)).setRegistryName(regNamePrefix + "_and"));
                }
            }
        }
*/
        if (BCSiliconItems.PLUG_PULSAR_ITEM.get() != null) {
            ItemStack output = new ItemStack(BCSiliconItems.PLUG_PULSAR_ITEM.get());

            ItemStack redstoneEngine;
            if (BCCoreBlocks.ENGINE_BC8.get() != null) {
                redstoneEngine = new ItemStack(BCCoreItems.ENGINE_ITEM_MAP.get(EnumEngineType.WOOD));
            } else {
                redstoneEngine = new ItemStack(Blocks.REDSTONE_BLOCK);
            }

            Set<IngredientStack> input = new HashSet<>();
            input.add(new IngredientStack(Ingredient.of(redstoneEngine)));
            input.add(new IngredientStack(Ingredient.of(Tags.Items.INGOTS_IRON), 2));
            AssemblyRecipe recipe = new AssemblyRecipeBasic("plug_pulsar", 1000 * MjAPI.MJ, input, output);
            AssemblyRecipeRegistry.register(recipe);
        }
        if (BCSiliconItems.PLUG_GATE_ITEM.get() != null) {
            IngredientStack lapis = new IngredientStack(Ingredient.of(Tags.Items.GEMS_LAPIS));
            makeGateAssembly(20_000, EnumGateMaterial.IRON, EnumGateModifier.NO_MODIFIER, EnumRedstoneChipset.IRON);
            makeGateAssembly(40_000, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.NO_MODIFIER,
                EnumRedstoneChipset.IRON, IngredientStack.of(Blocks.NETHER_BRICKS));
            makeGateAssembly(80_000, EnumGateMaterial.GOLD, EnumGateModifier.NO_MODIFIER, EnumRedstoneChipset.GOLD);

            makeGateModifierAssembly(40_000, EnumGateMaterial.IRON, EnumGateModifier.LAPIS, lapis);
            makeGateModifierAssembly(60_000, EnumGateMaterial.IRON, EnumGateModifier.QUARTZ,
                IngredientStack.of(EnumRedstoneChipset.QUARTZ.getStack()));
            makeGateModifierAssembly(80_000, EnumGateMaterial.IRON, EnumGateModifier.DIAMOND,
                IngredientStack.of(EnumRedstoneChipset.DIAMOND.getStack()));

            makeGateModifierAssembly(80_000, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.LAPIS, lapis);
            makeGateModifierAssembly(100_000, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.QUARTZ,
                IngredientStack.of(EnumRedstoneChipset.QUARTZ.getStack()));
            makeGateModifierAssembly(120_000, EnumGateMaterial.NETHER_BRICK, EnumGateModifier.DIAMOND,
                IngredientStack.of(EnumRedstoneChipset.DIAMOND.getStack()));

            makeGateModifierAssembly(100_000, EnumGateMaterial.GOLD, EnumGateModifier.LAPIS, lapis);
            makeGateModifierAssembly(140_000, EnumGateMaterial.GOLD, EnumGateModifier.QUARTZ,
                IngredientStack.of(EnumRedstoneChipset.QUARTZ.getStack()));
            makeGateModifierAssembly(180_000, EnumGateMaterial.GOLD, EnumGateModifier.DIAMOND,
                IngredientStack.of(EnumRedstoneChipset.DIAMOND.getStack()));
        }

        if (BCSiliconItems.PLUG_LIGHT_SENSOR_ITEM.get() != null) {
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("light-sensor", 500 * MjAPI.MJ,
                ImmutableSet.of(IngredientStack.of(Blocks.DAYLIGHT_DETECTOR)),
                new ItemStack(BCSiliconItems.PLUG_LIGHT_SENSOR_ITEM.get())));
        }

        if (BCSiliconItems.PLUG_FACADE_ITEM.get() != null) {
            AssemblyRecipeRegistry.register(FacadeAssemblyRecipes.INSTANCE);
            //ForgeRegistries.RECIPE_TYPES.register(FacadeSwapRecipe.INSTANCE);
        }

        if (BCSiliconItems.PLUG_LENS_ITEM.get() != null) {
            for (DyeColor colour : ColourUtil.COLOURS) {
                String name = String.format("lens-regular-%s", colour.getSerializedName());
                IngredientStack stainedGlass = IngredientStack.of("blockGlass" + ColourUtil.getName(colour));
                ImmutableSet<IngredientStack> input = ImmutableSet.of(stainedGlass);
                ItemStack output = BCSiliconItems.PLUG_LENS_ITEM.get().getStack(colour, false);
                AssemblyRecipeRegistry.register(new AssemblyRecipeBasic(name, 500 * MjAPI.MJ, input, output));

                name = String.format("lens-filter-%s", colour.getSerializedName());
                output = BCSiliconItems.PLUG_LENS_ITEM.get().getStack(colour, true);
                input = ImmutableSet.of(stainedGlass, IngredientStack.of(new ItemStack(Blocks.IRON_BARS)));
                AssemblyRecipeRegistry.register(new AssemblyRecipeBasic(name, 500 * MjAPI.MJ, input, output));
            }

            IngredientStack glass = IngredientStack.of(Tags.Blocks.GLASS);
            ImmutableSet<IngredientStack> input = ImmutableSet.of(glass);
            ItemStack output = BCSiliconItems.PLUG_LENS_ITEM.get().getStack(null, false);
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("lens-regular", 500 * MjAPI.MJ, input, output));

            output = BCSiliconItems.PLUG_LENS_ITEM.get().getStack(null, true);
            input = ImmutableSet.of(glass, IngredientStack.of(new ItemStack(Blocks.IRON_BARS)));
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("lens-filter", 500 * MjAPI.MJ, input, output));
        }

        if (BCSiliconItems.REDSTONE_CHIPSET_ITEMS != null) {
            ImmutableSet<IngredientStack> input = ImmutableSet.of(IngredientStack.of("dustRedstone"));
            ItemStack output = EnumRedstoneChipset.RED.getStack(1);
            AssemblyRecipeRegistry
                .register(new AssemblyRecipeBasic("redstone_chipset", 10000 * MjAPI.MJ, input, output));

            input = ImmutableSet.of(IngredientStack.of("dustRedstone"), IngredientStack.of("ingotIron"));
            output = EnumRedstoneChipset.IRON.getStack(1);
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("iron_chipset", 20000 * MjAPI.MJ, input, output));

            input = ImmutableSet.of(IngredientStack.of("dustRedstone"), IngredientStack.of("ingotGold"));
            output = EnumRedstoneChipset.GOLD.getStack(1);
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("gold_chipset", 40000 * MjAPI.MJ, input, output));

            input = ImmutableSet.of(IngredientStack.of("dustRedstone"), IngredientStack.of("gemQuartz"));
            output = EnumRedstoneChipset.QUARTZ.getStack(1);
            AssemblyRecipeRegistry.register(new AssemblyRecipeBasic("quartz_chipset", 60000 * MjAPI.MJ, input, output));

            input = ImmutableSet.of(IngredientStack.of("dustRedstone"), IngredientStack.of("gemDiamond"));
            output = EnumRedstoneChipset.DIAMOND.getStack(1);
            AssemblyRecipeRegistry
                .register(new AssemblyRecipeBasic("diamond_chipset", 80000 * MjAPI.MJ, input, output));
        }

        if (BCSiliconItems.gateCopier != null) {
            ImmutableSet.Builder<IngredientStack> input = ImmutableSet.builder();
            if (BCCoreItems.wrench != null) {
                input.add(IngredientStack.of(BCCoreItems.wrench));
            } else {
                input.add(IngredientStack.of(Items.STICK));
                input.add(IngredientStack.of(Items.IRON_INGOT));
            }

            if (BCSiliconItems.redstoneChipset != null) {
                input.add(IngredientStack.of(EnumRedstoneChipset.IRON.getStack(1)));
            } else {
                input.add(IngredientStack.of("dustRedstone"));
                input.add(IngredientStack.of("dustRedstone"));
                input.add(IngredientStack.of("ingotGold"));
            }

            AssemblyRecipeRegistry.register(
                new AssemblyRecipeBasic(
                    "gate_copier", 500 * MjAPI.MJ, input.build(), new ItemStack(BCSiliconItems.gateCopier)
                )
            );
        }

        scanForJsonRecipes();
    }

    private static void makeGateModifierAssembly(int multiplier, EnumGateMaterial material, EnumGateModifier modifier,
        IngredientStack... mods) {
        for (EnumGateLogic logic : EnumGateLogic.VALUES) {
            String name = String.format("gate-modifier-%s-%s-%s", logic, material, modifier);
            GateVariant variantFrom = new GateVariant(logic, material, EnumGateModifier.NO_MODIFIER);
            ItemStack toUpgrade = BCSiliconItems.plugGate.getStack(variantFrom);
            ItemStack output = BCSiliconItems.plugGate.getStack(new GateVariant(logic, material, modifier));
            Builder<IngredientStack> inputBuilder = new ImmutableSet.Builder<>();
            inputBuilder.add(new IngredientStack(new IngredientNBTBC(toUpgrade)));
            inputBuilder.add(mods);
            ImmutableSet<IngredientStack> input = inputBuilder.build();
            AssemblyRecipeRegistry.register((new AssemblyRecipeBasic(name, MjAPI.MJ * multiplier, input, output)));
        }
    }

    private static void makeGateAssembly(int multiplier, EnumGateMaterial material, EnumGateModifier modifier,
        EnumRedstoneChipset chipset, IngredientStack... additional) {
        ImmutableSet.Builder<IngredientStack> temp = ImmutableSet.builder();
        temp.add(new IngredientStack(new IngredientNBTBC(chipset.getStack())));
        temp.add(additional);
        ImmutableSet<IngredientStack> input = temp.build();

        String name = String.format("gate-and-%s-%s", material, modifier);
        ItemStack output = BCSiliconItems.PLUG_GATE_ITEM.get().getStack(new GateVariant(EnumGateLogic.AND, material, modifier));
        AssemblyRecipeRegistry.register((new AssemblyRecipeBasic(name, MjAPI.MJ * multiplier, input, output)));

        name = String.format("gate-or-%s-%s", material, modifier);
        output = BCSiliconItems.PLUG_GATE_ITEM.get().getStack(new GateVariant(EnumGateLogic.OR, material, modifier));
        AssemblyRecipeRegistry.register((new AssemblyRecipeBasic(name, MjAPI.MJ * multiplier, input, output)));
    }

    private static void makeGateRecipe(RecipeBuilderShaped builder, EnumGateMaterial material,
        EnumGateModifier modifier) {
        GateVariant variant = new GateVariant(EnumGateLogic.AND, material, modifier);
        builder.setResult(BCSiliconItems.PLUG_GATE_ITEM.get().getStack(variant));
        builder.registerNbtAware("buildcraftsilicon:plug_gate_create_" + material + "_" + modifier);
    }

    private static void scanForJsonRecipes() {
        final boolean[] failed = { false };
        for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
            JsonContext ctx = new JsonContext(mod.getModId());
            CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/assembly_recipes_pre_mj", null, (root, file) -> {
                try {
                    readAndAddJsonRecipe(ctx, root, file);
                    return true;
                } catch (IOException io) {
                    BCLog.logger.error("Couldn't read recipe " + root.relativize(file) + " from " + file, io);
                    failed[0] = true;
                    return true;
                }
            }, false, false);
        }

        Path configRoot = BCCoreConfig.configFolder.toPath().resolve("assembly_recipes_pre_mj");
        if (!Files.isDirectory(configRoot)) {
            try {
                Files.createDirectory(configRoot);
            } catch (IOException e) {
                BCLog.logger.warn("[silicon.assembly] Unable to create the folder " + configRoot);
                failed[0] = true;
                return;
            }
        }

        try {
            JsonContext ctx = new JsonContext("_config");
            Files.walkFileTree(configRoot, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        readAndAddJsonRecipe(ctx, configRoot, file);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                        failed[0] = true;
                    } catch (IOException io) {
                        io.printStackTrace();
                        failed[0] = true;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            BCLog.logger.warn("[silicon.assembly] Failed to walk the config folder " + configRoot, e);
            failed[0] = false;
        }

        if (failed[0]) {
            throw new IllegalStateException("Failed to read some assembly recipe files! Check the log for details");
        }
    }

    private static void readAndAddJsonRecipe(JsonContext ctx, Path root, Path file)
        throws JsonParseException, IOException {
        if (!file.toString().endsWith(".json")) {
            return;
        }

        String name = root.relativize(file).toString().replace("\\", "/");
        ResourceLocation key = new ResourceLocation(ctx.getModId(), name);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
            if (json == null || json.isJsonNull()) throw new JsonSyntaxException("Json is null (empty file?)");

            ItemStack output = CraftingHelper.getItemStack(json.getAsJsonObject("result"), ctx);
            long powercost = json.get("MJ").getAsLong() * MjAPI.MJ;

            ArrayList<IngredientStack> ingredients = new ArrayList<>();

            json.getAsJsonArray("components").forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                ingredients.add(new IngredientStack(CraftingHelper.getIngredient(object.get("ingredient"), ctx),
                    JsonUtils.getInt(object, "amount", 1)));
            });

            AssemblyRecipeRegistry.REGISTRY.put(key,
                new AssemblyRecipeBasic(key, powercost, ImmutableSet.copyOf(ingredients), output));
        }
    }
}
