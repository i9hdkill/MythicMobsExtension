package com.gmail.berndivader.mythicmobsext.compatibility.mobarena;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.garbagemule.MobArena.MobArenaHandler;
import com.gmail.berndivader.mythicmobsext.Main;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;

public
class 
MobArenaSupport
implements 
Listener 
{
	static String pluginName;
	public static MobArenaHandler mobarena;
	
	static {
		pluginName="MobArena";
		mobarena=new MobArenaHandler();
	}

	public MobArenaSupport() {
		Main.pluginmanager.registerEvents(this, Main.getPlugin());
		Main.logger.info("using "+pluginName);
	}

	@EventHandler
	public void onMythicMobsConditionsLoadEvent(MythicConditionLoadEvent e) {
		String conditionName = e.getConditionName().toLowerCase();
		if (conditionName.equals("inmobarena")) {
			SkillCondition c = new InMobArenaCondition(e.getConfig().getLine(), e.getConfig());
			e.register(c);
		}
	}
}