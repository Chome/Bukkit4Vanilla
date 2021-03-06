package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityPlayer;

//import org.bukkit.craftbukkit.BukkitServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;

public class BukkitFish extends AbstractProjectile implements Fish {
    public BukkitFish(BukkitServer server, EntityFishHook entity) {
        super(server, entity);
    }

    public LivingEntity getShooter() {
        if (getHandle().angler != null) {
            return (LivingEntity) this.getEntity(server, getHandle().angler);
        }

        return null;
    }

    public void setShooter(LivingEntity shooter) {
        if (shooter instanceof BukkitEntityHuman) {
            getHandle().angler = (EntityPlayer) ((BukkitEntityHuman) shooter).entity;
        }
    }

    @Override
    public EntityFishHook getHandle() {
        return (EntityFishHook) entity;
    }

    @Override
    public String toString() {
        return "BukkitFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }
}
