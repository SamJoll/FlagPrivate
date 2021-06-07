package ru.samjoll.main.Handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.samjoll.main.FlagPrivate;

public class PlayerHandler implements Listener {
//    Главный класс плагина
    FlagPrivate plugin;

//    Конструктор класса
    public PlayerHandler(FlagPrivate plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void PlayerJoinEvent(PlayerJoinEvent e) {
        final String playerName = e.getPlayer().getName();
        final String playerId = e.getPlayer().getUniqueId().toString();
        plugin.playersDB.WritePlayer(playerName, playerId);
    }
}
