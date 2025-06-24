package bluesteel42.copperpipes.util;

import bluesteel42.copperpipes.CopperPipes;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> COPPER_PIPES = createTag("copper_pipes");

        public static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(CopperPipes.MOD_ID, name));
        }
    }
}
