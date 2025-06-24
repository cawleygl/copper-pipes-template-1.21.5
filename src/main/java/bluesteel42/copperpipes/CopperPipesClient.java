package bluesteel42.copperpipes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import bluesteel42.copperpipes.entity.PipeScreen;

public class CopperPipesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register Screen
        HandledScreens.register(CopperPipes.PIPE_SCREEN_HANDLER, PipeScreen::new);
    }
}
