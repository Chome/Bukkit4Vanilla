package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntitySpider;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitSpider extends BukkitMonster implements Spider {

    public BukkitSpider(BukkitServer server, EntitySpider entity) {
        super(server, entity);
    }

    @Override
    public EntitySpider getHandle() {
        return (EntitySpider) entity;
    }

    @Override
    public String toString() {
        return "BukkitSpider";
    }

    public EntityType getType() {
        return EntityType.SPIDER;
    }
}
