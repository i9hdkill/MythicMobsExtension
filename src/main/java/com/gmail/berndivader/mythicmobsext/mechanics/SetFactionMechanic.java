package com.gmail.berndivader.mythicmobsext.mechanics;

import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.*;

@ExternalAnnotation(name="setfaction",author="BerndiVader")
public class SetFactionMechanic
extends
SkillMechanic
implements
ITargetedEntitySkill,
INoTargetSkill {
	
	protected String faction;
	public SetFactionMechanic(String line, MythicLineConfig mlc) {
		
		super(line, mlc);
		String f=mlc.getString(new String[] { "faction", "f" }, null);
		if (f!=null) {
			this.faction=SkillString.unparseMessageSpecialChars(f);
		}
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return this.castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		System.err.println("faction:"+this.faction);
		ActiveMob am = Utils.mobmanager.getMythicMobInstance(target);
		String f=Utils.parseMobVariables(this.faction,data,data.getCaster().getEntity(),target,null);
		if (am!=null) am.setFaction(f);
        target.getBukkitEntity().setMetadata("Faction",new FixedMetadataValue(Utils.mythicmobs,f));
		return true;
	}
}
