package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.event.BukkitEventFactory;
import keepcalm.mods.bukkit.bukkitAPI.item.BukkitItemStack;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.Item;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
/**
 * 
 * @author keepcalm
 * 
 * TODO: Fix up more events - Forge doesn't include all the required events.
 *
 */
public class ForgeEventHandler {
	@ForgeSubscribe
	/**
	 * Can't cancel this
	 */
	public void onEntityJoinWorld(EntityJoinWorldEvent ev) {

		if (ev.entity instanceof EntityLiving && !(ev.entity instanceof EntityPlayer)) {// || ev.entity instanceof EntityPlayerMP) {

			BukkitEventFactory.callCreatureSpawnEvent((EntityLiving) ev.entity, SpawnReason.DEFAULT);
		}
	}
	@ForgeSubscribe
	public void onItemExpire(ItemExpireEvent ev) {
		BukkitEventFactory.callItemDespawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onItemTossEvent(ItemTossEvent ev) {
		BukkitEventFactory.callItemSpawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onLivingAttack(LivingAttackEvent ev) {
		if (BukkitEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, DamageCause.valueOf(ev.source.getDamageType()), ev.ammount) == null) {

			ev.setCanceled(ev.isCancelable() ? true : false);
		}
	}
	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent ev) {
		BukkitEventFactory.callEntityDeathEvent(ev.entityLiving);
	}

	/*@ForgeSubscribe
	public void onLivingFall(LivingFallEvent ev) {
		BukkitEventFactory.callE
	}*/
	@ForgeSubscribe
	/**
	 * Needs fixing - field_blah is EntityExplosion or not?
	 * @param ev
	 */
	public void onLivingDamage(LivingHurtEvent ev) {
		DamageCause dc;
		DamageSource ds = ev.source;
		if (ds == ds.anvil)
			dc = DamageCause.CUSTOM;
		else if (ds == ds.cactus)
			dc = DamageCause.CONTACT;
		else if (ds == ds.drown)
			dc = DamageCause.DROWNING;
		// TODO fix this!
		else if (ds == ds.explosion)
			dc = DamageCause.BLOCK_EXPLOSION;
		else if (ds == ds.fall)
			dc = DamageCause.FALL;
		else if (ds == ds.fallingBlock)
			dc = DamageCause.FALL;
		else if (ds == ds.field_76375_l)
			dc = DamageCause.ENTITY_EXPLOSION;
		else if (ds == ds.generic)
			dc = DamageCause.CUSTOM;
		else if (ds == ds.inFire)
			dc = DamageCause.FIRE;
		else if (ds == ds.inWall)
			dc = DamageCause.SUFFOCATION;
		else if (ds == ds.lava)
			dc = DamageCause.LAVA;
		else if (ds == ds.magic)
			dc = DamageCause.MAGIC;
		else if (ds == ds.onFire)
			dc = DamageCause.FIRE_TICK;
		else if (ds == ds.outOfWorld)
			dc = DamageCause.VOID;
		else if (ds == ds.starve)
			dc = DamageCause.STARVATION;
		else if (ds == ds.wither)
			dc = DamageCause.WITHER;
		else
			dc = DamageCause.CUSTOM;
			
		BukkitEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, DamageCause.valueOf(ev.source.getDamageType()), ev.ammount);
	}
	@ForgeSubscribe
	public void onTarget(LivingSetAttackTargetEvent ev) {
		BukkitEventFactory.callEntityTargetEvent(ev.entity, ev.target, TargetReason.CUSTOM);
	}
	/*@ForgeSubscribe
	public void specialSpawn(LivingSpecialSpawnEvent ev) {
		BukkitEventFactory.callCreatureSpawnEvent(ev.entityLiving, spawnReason)
	}
	@ForgeSubscribe
	public void onCartCollide(MinecartCollisionEvent ev) {
		BukkitEventFactory.
	}
	 */
	@ForgeSubscribe
	/**
	 * Only called when a player fires
	 * Forge doesn't give us the EntityArrow.
	 * @param ev
	 */
	public void bowFire(ArrowLooseEvent ev) {
		BukkitEventFactory.callEntityShootBowEvent(ev.entityPlayer, ev.bow, null, ev.charge);
	}

	@ForgeSubscribe
	public void playerVEntity(AttackEntityEvent ev) {
		BukkitEventFactory.callEntityDamageEvent(ev.entityPlayer, ev.target, DamageCause.ENTITY_ATTACK, ev.entityPlayer.inventory.getDamageVsEntity(ev.target));
	}
	/*
	@ForgeSubscribe
	public void bonemeal(BonemealEvent ev) {
	}*/
	@ForgeSubscribe
	public void playerSaysHai(PlayerInteractEvent ev) {
		if (ev.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && ev.entityPlayer.inventory.getCurrentItem() != null) {
			MovingObjectPosition mop = PlayerUtilities.getTargetBlock((EntityPlayerMP) ev.entityPlayer);
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;

			World playerWorld = ev.entityPlayer.worldObj;
			BukkitBlock targ = new BukkitBlock(new BukkitChunk(playerWorld.getChunkFromBlockCoords(x,z)),x,y,z);

			BlockDamageEvent ev2 = new BlockDamageEvent((Player) BukkitEntity.getEntity(BukkitServer.instance(), ev.entityPlayer), targ, new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), ev.entityPlayer.capabilities.isCreativeMode);
			
			Bukkit.getPluginManager().callEvent(ev2);
			if (ev2.isCancelled())
				ev.setCanceled(true);
		}
		else if (ev.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			MovingObjectPosition mop = PlayerUtilities.getTargetBlock((EntityPlayerMP) ev.entityPlayer);
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;
			if (mop.sideHit == -1) {
				return;
			}
			ForgeDirection fd = ForgeDirection.getOrientation(mop.sideHit);
			x = x + fd.offsetX;
			y = y + fd.offsetY;
			z = z + fd.offsetZ;
			World playerWorld = ev.entityPlayer.worldObj;
			if (playerWorld.blockHasTileEntity(x, y, z) && !ev.entityPlayer.isSneaking()) {


			}
			else {

				BukkitBlock targ = new BukkitBlock(new BukkitChunk(playerWorld.getChunkFromBlockCoords(x,z)),x,y,z);
				BukkitBlock against = new BukkitBlock((BukkitChunk) targ.getChunk(), mop.blockX, mop.blockY, mop.blockZ);
				int radius = BukkitServer.instance().getHandle().getSpawnProtectionSize();
				ChunkCoordinates spawn = playerWorld.getSpawnPoint();

				int minx = spawn.posX - radius;
				int maxx = spawn.posX + radius;
				int maxy = spawn.posY + radius;
				int miny = spawn.posY - radius;
				int minz = spawn.posZ - radius;
				int maxz = spawn.posZ + radius;

				boolean isPlaceAllowed = (x > minx && x < maxx) && (y > miny && y < miny) && (z > minz && z < minz);
				BlockPlaceEvent ev2 = new BlockPlaceEvent(targ, targ.getState(), against, new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), (Player) BukkitEntity.getEntity(BukkitServer.instance(), ev.entityPlayer), isPlaceAllowed);
				Bukkit.getPluginManager().callEvent(ev2);
				if (ev2.isCancelled()) 
					ev.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
	public void pickUp(EntityItemPickupEvent ev) {
		if (BukkitEventFactory.callItemDespawnEvent(ev.item).isCancelled())
			ev.setCanceled(true);
	}

	@ForgeSubscribe
	public void fillBukkit(FillBucketEvent ev) {
		if (BukkitEventFactory.callPlayerBucketFillEvent((EntityPlayerMP) ev.entityPlayer, ev.target.blockX, ev.target.blockY, ev.target.blockZ, ev.target.sideHit, ev.current,Item.itemsList[ev.result.itemID]).isCancelled())
			ev.setCanceled(true);
	}

	@ForgeSubscribe
	public void interactEvent(PlayerInteractEvent ev) {
		BukkitEventFactory.callPlayerInteractEvent((EntityPlayerMP) ev.entityPlayer, Action.valueOf(ev.action.toString()), ev.entityPlayer.inventory.getCurrentItem());
	}
	/*
	@ForgeSubscribe
	public void goToSleep(PlayerSleepInBedEvent ev) {

	}*/
	// BROKEN - FIXME TODO etc
	@ForgeSubscribe
	public void chunkLoadEvent(ChunkEvent.Load ev) {
		final org.bukkit.event.world.ChunkLoadEvent c = new org.bukkit.event.world.ChunkLoadEvent(new BukkitChunk(ev.getChunk()), false);

		try {
			BukkitServer.instance().getWorld(0);
		}
		catch (NullPointerException e) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {}

		}
		Bukkit.getPluginManager().callEvent(c);
	}

	@ForgeSubscribe
	public void chunkUnloadEvent(ChunkEvent.Unload ev) {
		org.bukkit.event.world.ChunkUnloadEvent c = new org.bukkit.event.world.ChunkUnloadEvent(new BukkitChunk(ev.getChunk()));
		Bukkit.getPluginManager().callEvent(c);
	}

}
