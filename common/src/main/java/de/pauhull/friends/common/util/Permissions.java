package de.pauhull.friends.common.util;

public class Permissions {

    public static class Party {
        public static final String JOIN = "party.join";
        public static final String LIST = "party.list";
        public static final String LEAVE = "party.leave";
        public static final String PARTYCHAT = "party.partychat";
        public static final String KICK = "party.kick";
        public static final String JUMP = "party.jump";
        public static final String INVITE = "party.invite";
        public static final String CREATE = "party.create";
        public static final String SETOWNER = "party.setowner";
        public static final String DELETE = "party.delete";
        public static final String TOGGLE_INVITES = "party.toggleinvites";
        public static final String PREMIUM = "party.premium";
    }

    public static class Friends {
        public static final String ACCEPT = "friends.accept";
        public static final String ACCEPT_ALL = "friends.accept.all";
        public static final String ADD = "friends.add";
        public static final String DENY = "friends.deny";
        public static final String DENY_ALL = "friends.deny.all";
        public static final String JUMP = "friends.jump";
        public static final String MSG = "friends.msg";
        public static final String RELOAD = "friends.reload";
        public static final String REMOVE = "friends.remove";
        public static final String STATUS = "friends.status";
        public static final String TOGGLE = "friends.toggle";
    }

}
