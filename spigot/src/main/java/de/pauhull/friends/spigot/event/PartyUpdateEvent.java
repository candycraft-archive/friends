package de.pauhull.friends.spigot.event;

import de.pauhull.friends.common.party.Party;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Paul
 * on 08.01.2019
 *
 * @author pauhull
 */
public class PartyUpdateEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private Party party;

    @Getter
    private Action action;

    @Getter
    private EnumActionType type;

    public PartyUpdateEvent(Party party, Action action) {
        this.party = party;
        this.action = action;
        this.type = action.getType();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public enum EnumActionType {
        OWNER_SET
    }

    public static abstract class Action {

        @Getter
        protected EnumActionType type;

        protected Action(EnumActionType type) {
            this.type = type;
        }

    }

    public static class ActionOwnerSet extends Action {

        @Getter
        private String oldOwner, newOwner;

        public ActionOwnerSet(String oldOwner, String newOwner) {
            super(EnumActionType.OWNER_SET);
            this.oldOwner = oldOwner;
            this.newOwner = newOwner;
        }

    }

}
