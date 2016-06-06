package buildcraft.builders.tile;

import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import buildcraft.api.core.EnumPipePart;
import buildcraft.core.Box;
import buildcraft.lib.bpt.builder.BuilderAnimationManager;
import buildcraft.lib.bpt.builder.BuilderAnimationManager.EnumBuilderAnimMessage;
import buildcraft.lib.net.command.IPayloadWriter;
import buildcraft.lib.tile.TileBCInventory_Neptune;
import buildcraft.lib.tile.item.ItemHandlerManager.EnumAccess;

public class TileBuilder_Neptune extends TileBCInventory_Neptune implements ITickable {

    public static final int NET_BOX = 10;
    public static final int NET_PATH = 11;
    public static final int NET_ANIM_ITEM = 12;
    public static final int NET_ANIM_BLOCK = 13;
    public static final int NET_ANIM_FLUID = 14;
    public static final int NET_ANIM_POWER = 15;

    public final BuilderAnimationManager animation = new BuilderAnimationManager(this::sendMessage);
    private final IItemHandlerModifiable invBlueprint = addInventory("blueprint", 1, EnumAccess.BOTH, EnumPipePart.VALUES);
    private TileBuilderAccessor builder = null;
    private List<BlockPos> path = null;
    private Box box = null;

    @Override
    protected void onSlotChange(IItemHandlerModifiable itemHandler, int slot, ItemStack before, ItemStack after) {
        if (itemHandler == invBlueprint) {
            // Update bpt + builder
            if (after == null) {
                // builder.releaseAll();
                builder = null;
                animation.reset();
            }
        }
    }

    @Override
    public void update() {
        if (builder != null) {
            builder.update();
            animation.update();
        }
    }

    private void advanceBuilder() {

    }

    private void sendMessage(EnumBuilderAnimMessage type, IPayloadWriter writer) {
        int id;
        if (type == EnumBuilderAnimMessage.BLOCK) id = NET_ANIM_BLOCK;
        else if (type == EnumBuilderAnimMessage.ITEM) id = NET_ANIM_ITEM;
        else if (type == EnumBuilderAnimMessage.FLUID) id = NET_ANIM_FLUID;
        else if (type == EnumBuilderAnimMessage.POWER) id = NET_ANIM_POWER;
        else throw new IllegalArgumentException("Unknown type " + type);
        this.createAndSendMessage(false, id, writer);
    }

    @Override
    public void writePayload(int id, PacketBuffer buffer, Side side) {
        super.writePayload(id, buffer, side);
        if (side == Side.SERVER) {
            if (id == NET_RENDER_DATA) {
                animation.writeStatePayload(buffer);
                writePayload(NET_BOX, buffer, side);
                writePayload(NET_PATH, buffer, side);
            } else if (id == NET_BOX) {

            } else if (id == NET_PATH) {

            }
        }
    }

    @Override
    public void readPayload(int id, PacketBuffer buffer, Side side) throws IOException {
        super.readPayload(id, buffer, side);
        if (side == Side.CLIENT) {
            if (id == NET_RENDER_DATA) {
                animation.receiveMessage(EnumBuilderAnimMessage.STATE, buffer);
                readPayload(NET_BOX, buffer, side);
                readPayload(NET_PATH, buffer, side);
            } else if (id == NET_BOX) {

            } else if (id == NET_PATH) {

            }
            // All animation types
            else if (id == NET_ANIM_ITEM) animation.receiveMessage(EnumBuilderAnimMessage.ITEM, buffer);
            else if (id == NET_ANIM_BLOCK) animation.receiveMessage(EnumBuilderAnimMessage.BLOCK, buffer);
            else if (id == NET_ANIM_FLUID) animation.receiveMessage(EnumBuilderAnimMessage.FLUID, buffer);
            else if (id == NET_ANIM_POWER) animation.receiveMessage(EnumBuilderAnimMessage.POWER, buffer);
        }
    }
}
