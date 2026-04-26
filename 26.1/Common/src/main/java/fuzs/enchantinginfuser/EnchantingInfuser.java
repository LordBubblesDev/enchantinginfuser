package fuzs.enchantinginfuser;

import fuzs.enchantinginfuser.config.ServerConfig;
import fuzs.enchantinginfuser.init.ModRegistry;
import fuzs.enchantinginfuser.network.ClientboundInfuserEnchantmentsMessage;
import fuzs.enchantinginfuser.network.client.ServerboundEnchantmentLevelMessage;
import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.common.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.common.api.event.v1.BuildCreativeModeTabContentsCallback;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantingInfuser implements ModConstructor {
    public static final String MOD_ID = "enchantinginfuser";
    public static final String MOD_NAME = "Enchanting Infuser";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            Identifier.withDefaultNamespace("functional_blocks"));

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);
    public static final Identifier TREASURE_ENCHANTMENTS_LOCATION = EnchantingInfuser.id("treasure_enchantments");

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        EventInvoker<?> invoker = BuildCreativeModeTabContentsCallback.buildCreativeModeTabContents(FUNCTIONAL_BLOCKS_TAB);
        Class<?> callbackClass = BuildCreativeModeTabContentsCallback.class;
        Object callback = java.lang.reflect.Proxy.newProxyInstance(
                callbackClass.getClassLoader(),
                new Class<?>[]{callbackClass},
                (proxy, method, args) -> {
                    if ("onBuildCreativeModeTabContents".equals(method.getName())) {
                        Object output = args[2];
                        java.lang.reflect.Method acceptMethod = output.getClass().getMethod("accept", ItemLike.class);
                        acceptMethod.invoke(output, ModRegistry.INFUSER_ITEM.value());
                        acceptMethod.invoke(output, ModRegistry.ADVANCED_INFUSER_ITEM.value());
                    }
                    return null;
                }
        );
        @SuppressWarnings({"rawtypes", "unchecked"})
        EventInvoker rawInvoker = invoker;
        rawInvoker.register(callback);
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToClient(ClientboundInfuserEnchantmentsMessage.class,
                ClientboundInfuserEnchantmentsMessage.STREAM_CODEC);
        context.playToServer(ServerboundEnchantmentLevelMessage.class, ServerboundEnchantmentLevelMessage.STREAM_CODEC);
    }

    @Override
    public void onAddDataPackFinders(PackRepositorySourcesContext context) {
        context.registerBuiltInPack(TREASURE_ENCHANTMENTS_LOCATION, Component.literal("Treasure Enchantments"), false);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
