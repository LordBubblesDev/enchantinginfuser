package fuzs.enchantinginfuser.client;

import fuzs.enchantinginfuser.client.gui.screens.inventory.InfuserScreen;
import fuzs.enchantinginfuser.client.renderer.blockentity.InfuserRenderer;
import fuzs.enchantinginfuser.init.ModRegistry;
import fuzs.enchantinginfuser.world.level.block.InfuserBlock;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.common.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.common.api.client.gui.v2.tooltip.ItemTooltipRegistry;

public class EnchantingInfuserClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        ItemTooltipRegistry.BLOCK.registerItemTooltip(InfuserBlock.class, InfuserBlock::getDescriptionComponent);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        // 26.1 makes MenuScreens.ScreenConstructor non-public. Register through reflection
        // so this keeps working across mappings without stripping screen functionality.
        try {
            Class<?> screenConstructorClass = Class.forName("net.minecraft.client.gui.screens.MenuScreens$ScreenConstructor");
            Object screenConstructorProxy = java.lang.reflect.Proxy.newProxyInstance(
                    screenConstructorClass.getClassLoader(),
                    new Class<?>[]{screenConstructorClass},
                    (proxy, method, args) -> {
                        if ("create".equals(method.getName())) {
                            return new InfuserScreen((fuzs.enchantinginfuser.world.inventory.InfuserMenu) args[0],
                                    (net.minecraft.world.entity.player.Inventory) args[1],
                                    (net.minecraft.network.chat.Component) args[2]);
                        }
                        return null;
                    }
            );
            java.lang.reflect.Method registerMethod = context.getClass()
                    .getMethod("registerMenuScreen", net.minecraft.world.inventory.MenuType.class, screenConstructorClass);
            registerMethod.invoke(context, ModRegistry.INFUSING_MENU_TYPE.value(), screenConstructorProxy);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register infuser menu screen", e);
        }
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.INFUSER_BLOCK_ENTITY_TYPE.value(), InfuserRenderer::new);
    }
}
