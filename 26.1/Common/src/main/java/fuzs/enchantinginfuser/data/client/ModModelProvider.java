package fuzs.enchantinginfuser.data.client;

import fuzs.enchantinginfuser.init.ModRegistry;
import fuzs.puzzleslib.common.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ModModelProvider extends AbstractModelProvider {
    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        this.createEnchantingInfuserBlock(ModRegistry.INFUSER_BLOCK.value(), blockModelGenerators);
        this.createEnchantingInfuserBlock(ModRegistry.ADVANCED_INFUSER_BLOCK.value(), blockModelGenerators);
    }

    public final void createEnchantingInfuserBlock(Block block, BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(block, Blocks.ENCHANTING_TABLE);
    }
}
