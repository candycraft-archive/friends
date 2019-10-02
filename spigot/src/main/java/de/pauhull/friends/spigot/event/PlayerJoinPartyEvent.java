package de.pauhull.friends.spigot.event;

import de.pauhull.friends.common.party.Party;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Paul
 * on 14.12.2018
 *
 * @author pauhull
 */
public class PlayerJoinPartyEvent extends PlayerEvent {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private Party party;

    public PlayerJoinPartyEvent(Player player, Party party) {
        super(player);
        this.party = party;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
