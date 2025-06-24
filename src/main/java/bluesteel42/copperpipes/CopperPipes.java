package bluesteel42.copperpipes;

import bluesteel42.copperpipes.block.ModBlocks;
import bluesteel42.copperpipes.block.PipeScreenHandler;
import bluesteel42.copperpipes.entity.ModBlockEntities;
import bluesteel42.copperpipes.registries.ModRegistries;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bluesteel42.copperpipes.block.ModBlocks.PIPE;
import static net.minecraft.block.entity.StructureBoxRendering.RenderMode.BOX;

public class CopperPipes implements ModInitializer {
	public static final String MOD_ID = "copper-pipes";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ScreenHandlerType<PipeScreenHandler> PIPE_SCREEN_HANDLER =
			Registry.register(
					Registries.SCREEN_HANDLER,
					Identifier.of(MOD_ID, "pipe_block"),
					new ScreenHandlerType<>(PipeScreenHandler::new, FeatureSet.empty()
				)
			);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModRegistries.registerOxidizables();
		LOGGER.info("Hello Fabric world!");
	}
}