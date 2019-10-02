package de.pauhull.friends.bungee.util;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class MessageManager {

    @Getter
    private Configuration config;

    @Getter
    private String playerDoesntExist, noPermissions, onlyPlayers;

    @Getter
    private String friendsPrefix, requestReceived, alreadyRequested, requestSent, requestWithdrawn,
            openRequests, youAccepted, youDenied, requestAccepted, requestDenied, noRequest, alreadyFriend, noFriend, friendRemoved,
            friendsNotSelf, noRequests, requestsAccepted, requestsDenied, notOnline, noMessages, messagesOn,
            messagesOff, messagesDisabled, messagesDisabledSelf, nowOnline, nowOffline, notificationsOn, notificationsOff,
            noJumping, sameServer, jumpingOn, jumpingOff, requestsOn, requestsOff, receivesNoRequests, unallowedCharacters,
            statusTooLong, statusChanged, yourStatus;

    @Getter
    private String partyPrefix, partyCreated2, youInvited, gotInvited, partyNotSelf, alreadyInParty;

    public MessageManager load(Configuration config) {
        this.config = config;

        this.noPermissions = getFromConfig("Messages.NoPermissions");
        this.onlyPlayers = getFromConfig("Messages.OnlyPlayers");
        this.playerDoesntExist = getFromConfig("Messages.PlayerDoesntExist");
        this.notOnline = getFromConfig("Messages.NotOnline");

        this.friendsPrefix = "§f[§3Freunde§f] §7";
        this.alreadyFriend = getFromConfig("Messages.Friends.AlreadyFriend");
        this.alreadyRequested = getFromConfig("Messages.Friends.AlreadyRequested");
        this.friendRemoved = getFromConfig("Messages.Friends.FriendRemoved");
        this.jumpingOff = getFromConfig("Messages.Friends.JumpingOff");
        this.jumpingOn = getFromConfig("Messages.Friends.JumpingOn");
        this.messagesDisabled = getFromConfig("Messages.Friends.FriendsDisabled");
        this.messagesDisabledSelf = getFromConfig("Messages.Friends.FriendsDisabledSelf");
        this.messagesOff = getFromConfig("Messages.Friends.FriendsOff");
        this.messagesOn = getFromConfig("Messages.Friends.FriendsOn");
        this.noFriend = getFromConfig("Messages.Friends.NoFriend");
        this.noJumping = getFromConfig("Messages.Friends.NoJumping");
        this.noMessages = getFromConfig("Messages.Friends.NoMessages");
        this.noRequest = getFromConfig("Messages.Friends.NoRequest");
        this.noRequests = getFromConfig("Messages.Friends.NoRequests");
        this.notificationsOff = getFromConfig("Messages.Friends.NotificationsOff");
        this.notificationsOn = getFromConfig("Messages.Friends.NotificationsOn");
        this.friendsNotSelf = getFromConfig("Messages.Friends.NotSelf");
        this.nowOffline = getFromConfig("Messages.Friends.NowOffline");
        this.nowOnline = getFromConfig("Messages.Friends.NowOnline");
        this.openRequests = getFromConfig("Messages.Friends.OpenRequests");
        this.receivesNoRequests = getFromConfig("Messages.Friends.ReceivesNoRequests");
        this.requestAccepted = getFromConfig("Messages.Friends.RequestAccepted");
        this.requestDenied = getFromConfig("Messages.Friends.RequestDenied");
        this.requestReceived = getFromConfig("Messages.Friends.RequestReceived");
        this.requestsAccepted = getFromConfig("Messages.Friends.RequestsAccepted");
        this.requestsDenied = getFromConfig("Messages.Friends.RequestsDenied");
        this.requestSent = getFromConfig("Messages.Friends.RequestSent");
        this.requestsOff = getFromConfig("Messages.Friends.RequestsOff");
        this.requestsOn = getFromConfig("Messages.Friends.RequestsOn");
        this.requestWithdrawn = getFromConfig("Messages.Friends.RequestWithdrawn");
        this.sameServer = getFromConfig("Messages.Friends.SameServer");
        this.statusChanged = getFromConfig("Messages.Friends.StatusChanged");
        this.statusTooLong = getFromConfig("Messages.Friends.StatusTooLong");
        this.unallowedCharacters = getFromConfig("Messages.Friends.UnallowedCharacters");
        this.youAccepted = getFromConfig("Messages.Friends.YouAccepted");
        this.youDenied = getFromConfig("Messages.Friends.YouDenied");
        this.yourStatus = getFromConfig("Messages.Friends.YourStatus");

        this.partyPrefix = "§f[§5Party§f] §7";
        this.partyCreated2 = getFromConfig("Messages.Party.PartyCreated2");
        this.youInvited = getFromConfig("Messages.Party.YouInvited");
        this.gotInvited = getFromConfig("Messages.Party.GotInvited");
        this.partyNotSelf = getFromConfig("Messages.Party.NotSelf");
        this.alreadyInParty = getFromConfig("Messages.Party.AlreadyInParty");

        return this;
    }

    private String getFromConfig(String key) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(key));
    }

}
