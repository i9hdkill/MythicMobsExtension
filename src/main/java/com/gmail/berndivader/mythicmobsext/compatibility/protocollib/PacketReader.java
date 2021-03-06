package com.gmail.berndivader.mythicmobsext.compatibility.protocollib;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.server.TemporaryPlayer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.compatibility.protocollib.wrapper.WrapperPlayServerEntityMetadata;
import com.gmail.berndivader.mythicmobsext.compatibility.protocollib.wrapper.WrapperPlayServerEntityStatus;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

public
class
PacketReader
implements
PacketListener {
	
	ListeningWhitelist outgoing;
	ListeningWhitelist incoming;
	
	public PacketReader() {
		
		this.incoming=ListeningWhitelist.EMPTY_WHITELIST;
		
		this.outgoing=ListeningWhitelist.newBuilder()
				.priority(ListenerPriority.LOWEST)
				.types(new PacketType[] {
						PacketType.Play.Server.ENTITY_METADATA,
						PacketType.Play.Server.ENTITY_STATUS,
					})
				.gamePhase(GamePhase.PLAYING)
				.options(new ListenerOptions[0])
				.build();
	}

	@Override
	public Plugin getPlugin() {
		return Main.getPlugin();
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return incoming;
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return outgoing;
	}

	@Override
	public void onPacketReceiving(PacketEvent packet_event) {
	}

	@Override
	public void onPacketSending(PacketEvent packet_event) {
		if(packet_event.isCancelled()||packet_event.getPlayer() instanceof TemporaryPlayer) return;
		Entity e=null;
		switch(packet_event.getPacketType().getCurrentId()) {
		case 28:
			WrapperPlayServerEntityStatus entity_status=new WrapperPlayServerEntityStatus(packet_event.getPacket().deepClone());
			if(entity_status.getEntityStatus()==37) {
				if((e=entity_status.getEntity(packet_event))!=null&&(e instanceof LivingEntity)&&e.hasMetadata(Utils.meta_NOSUNBURN)) {
					NMSUtils.setFireProofEntity(e,true);;
					packet_event.setCancelled(true);
				}
			}
			break;
		case 63:
			WrapperPlayServerEntityMetadata entity_meta=new WrapperPlayServerEntityMetadata(packet_event.getPacket().deepClone());
			if((e=entity_meta.getEntity(packet_event))!=null&&(e instanceof LivingEntity)&&e.hasMetadata(Utils.meta_NOSUNBURN)) {
				List<WrappedWatchableObject>watchables=entity_meta.getMetadata();
				if(watchables.size()>0) {
					WrappedWatchableObject watchable=watchables.get(0);
					if(watchable.getValue() instanceof Byte) {
						byte b=(byte)((byte)watchable.getValue()&~0x1);
						watchable.setValue(b);
						entity_meta.setMetadata(watchables);
						packet_event.setPacket(entity_meta.getHandle());
					}
				}
			};
			break;
		}
	}

}
