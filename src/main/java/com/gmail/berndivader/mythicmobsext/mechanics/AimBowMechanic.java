package com.gmail.berndivader.mythicmobsext.mechanics;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.Vec2D;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

@ExternalAnnotation(name="bowaimbot",author="BerndiVader")
public 
class 
AimBowMechanic 
extends
SkillMechanic
implements
ITargetedEntitySkill
{
	boolean aim_nearest;
	static String meta_str;
	
	static {
		meta_str="MMEAIMBOT";
	}

	public AimBowMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		ASYNC_SAFE=false;
		aim_nearest=mlc.getBoolean("aimnearest",false);
	}
	
	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity a_target) {
		if(data.getCaster().getEntity().isPlayer()&&a_target.isLiving()) {
			final Player player=(Player)data.getCaster().getEntity().getBukkitEntity();
			
			if(!player.hasMetadata(meta_str)) {
				final LivingEntity target=(LivingEntity)a_target.getBukkitEntity();
				player.setMetadata(meta_str,new FixedMetadataValue(Main.getPlugin(),true));
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if(player==null||player.isDead()||!player.isOnline()||!player.isHandRaised()||!player.hasMetadata(meta_str)) {
							if(player.hasMetadata(meta_str)) player.removeMetadata(meta_str,Main.getPlugin());
							this.cancel();
						} else {
							float velocity=Utils.getBowTension(player);
							if(velocity>0.1f) {
								Vec3D target_position=Volatile.handler.getPredictedMotion(player,target,5.0f);
								Vec2D direction=MathUtils.calculateDirectionVec2D(target_position,velocity,0.006f);
								float yaw=(float)direction.getX();
								float pitch=(float)direction.getY();
								if(!Float.isNaN(yaw)&&!Float.isNaN(pitch)) Volatile.handler.playerConnectionLookAt(player,yaw,pitch);
							}
						}
					}
				}.runTaskTimer(Main.getPlugin(),1l,1l);
			}
			return true;
		}
		return false;
	}
	
	

}
