package trapcraft;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import trapcraft.api.Constants;
import trapcraft.block.tileentity.BearTrapTileEntity;
import trapcraft.block.tileentity.CopperVaccumTileEntity;
import trapcraft.block.tileentity.FanTileEntity;
import trapcraft.block.tileentity.IgniterTileEntity;
import trapcraft.block.tileentity.IronFanTileEntity;
import trapcraft.block.tileentity.MagneticChestTileEntity;

import java.util.Set;

public class TrapcraftTileEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<MagneticChestTileEntity>> MAGNETIC_CHEST = TILE_ENTITIES.register("magnetic_chest", () -> new BlockEntityType<>(MagneticChestTileEntity::new, Set.of(TrapcraftBlocks.MAGNETIC_CHEST.get()), null));
    public static final RegistryObject<BlockEntityType<FanTileEntity>> FAN = TILE_ENTITIES.register("fan", () -> new BlockEntityType<>(FanTileEntity::new, Set.of(TrapcraftBlocks.FAN.get()), null));
    public static final RegistryObject<BlockEntityType<IronFanTileEntity>> IRONFAN = TILE_ENTITIES.register("iron_fan", () -> new BlockEntityType<>(IronFanTileEntity::new, Set.of(TrapcraftBlocks.IRONFAN.get()), null));
    public static final RegistryObject<BlockEntityType<CopperVaccumTileEntity>> COPPERVACCUM = TILE_ENTITIES.register("copper_vaccum", () -> new BlockEntityType<>(CopperVaccumTileEntity::new, Set.of(TrapcraftBlocks.COPPERVACCUM.get()), null));
    public static final RegistryObject<BlockEntityType<BearTrapTileEntity>> BEAR_TRAP = TILE_ENTITIES.register("bear_trap", () -> new BlockEntityType<>(BearTrapTileEntity::new, Set.of(TrapcraftBlocks.BEAR_TRAP.get()), null));
    public static final RegistryObject<BlockEntityType<IgniterTileEntity>> IGNITER = TILE_ENTITIES.register("igniter", () -> new BlockEntityType<>(IgniterTileEntity::new, Set.of(TrapcraftBlocks.IGNITER.get()), null));
}
