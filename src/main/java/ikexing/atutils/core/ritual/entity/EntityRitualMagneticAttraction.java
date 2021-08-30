package ikexing.atutils.core.ritual.entity;

import com.google.common.collect.Maps;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import epicsquid.roots.entity.ritual.EntityRitualBase;
import ikexing.atutils.ATUtils;
import ikexing.atutils.core.ritual.RitualMagneticAttraction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityRitualMagneticAttraction extends EntityRitualBase {

    private final RitualMagneticAttraction ritual;
    private final Map<BlockPos, String> posList = Maps.newHashMap();

    public EntityRitualMagneticAttraction(World worldIn) {
        super(worldIn);
        getDataManager().register(lifetime, ATUtils.ritualMa.getDuration() + 20);
        ritual = (RitualMagneticAttraction) ATUtils.ritualMa;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted % 20 == 0) {
            getBlockOre();
        }
        if (ticksExisted == ritual.getDuration()) {
            if (!world.isRemote) {
                for (Map.Entry<BlockPos, String> entry : posList.entrySet()) {
                    BlockPos pos = entry.getKey();
                    ItemStack stack = OreDictionary.getOres("dust" + entry.getValue()).get(0);
                    stack.setCount(world.rand.nextInt(7));

                    world.setBlockToAir(pos);
                    world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
                }
            }
        }
        System.out.println(ticksExisted + "");
    }

    private void getBlockOre() {
        posList.clear();

        BlockPos posA = new BlockPos(posX + ritual.radius_x, posY + ritual.radius_y, posZ + ritual.radius_z);
        BlockPos posB = new BlockPos(posX - ritual.radius_x, posY - ritual.radius_y, posZ - ritual.radius_z);

        Iterable<BlockPos> allInBox = BlockPos.getAllInBox(posA, posB);
        for (BlockPos inBox : allInBox) {
            IBlockState state = world.getBlockState(inBox);
            IItemStack crtStack = CraftTweakerMC.getIItemStack(state.getBlock().getItem(world, inBox, state));
            List<IOreDictEntry> ores = Objects.nonNull(crtStack) ? crtStack.getOres() : Collections.emptyList();
            for (IOreDictEntry ore : ores) {
                String name = ore.getName();
                if (name.equals("oreIron") || name.equals("oreCrudeSteel") || name.equals("oreNickel")) {
                    posList.put(inBox, name.replace("ore", ""));
                }
            }
        }
    }
}
