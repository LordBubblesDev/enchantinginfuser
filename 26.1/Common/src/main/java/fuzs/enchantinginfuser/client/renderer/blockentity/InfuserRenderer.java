package fuzs.enchantinginfuser.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.enchantinginfuser.client.renderer.blockentity.state.InfuserRenderState;
import fuzs.enchantinginfuser.world.level.block.entity.InfuserBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class InfuserRenderer implements BlockEntityRenderer<InfuserBlockEntity, InfuserRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final EnchantTableRenderer enchantTableRenderer;

    public InfuserRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.enchantTableRenderer = new EnchantTableRenderer(context);
    }

    @Override
    public InfuserRenderState createRenderState() {
        return new InfuserRenderState();
    }

    @Override
    public void extractRenderState(InfuserBlockEntity blockEntity, InfuserRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        this.enchantTableRenderer.extractRenderState(blockEntity,
                renderState,
                partialTick,
                cameraPosition,
                crumblingOverlay);
        renderState.item.clear();
        this.itemModelResolver.updateForTopItem(renderState.item,
                blockEntity.getItem(0),
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                0);
    }

    @Override
    public void submit(InfuserRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        this.enchantTableRenderer.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
        this.submitItem(renderState, poseStack, submitNodeCollector);
    }

    // Keep the custom floating item above the infuser on 26.1.
    private void submitItem(InfuserRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (renderState.open > 0.0F) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.0F, 0.5F);
            float hoverOffset = Mth.sin(renderState.time / 10.0F) * 0.1F + 0.1F;
            AABB aabb = renderState.item.getModelBoundingBox();
            float modelYScale = -((float) aabb.minY) + 0.0625F;
            poseStack.translate(0.0,
                    hoverOffset + modelYScale * renderState.open - 0.15F * (1.0F - renderState.open),
                    0.0);
            float scale = renderState.open * 0.8F + 0.2F;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotation(renderState.time / 20.0F));
            renderState.item.submit(poseStack,
                    submitNodeCollector,
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0);
            poseStack.popPose();
        }
    }
}
