package ru.samjoll.main.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.samjoll.main.FlagPrivate;

public class RemoveFlagMemberCommand implements CommandExecutor {
//    Главный класс плагина
    FlagPrivate plugin;

//    Конструктор класса
    public RemoveFlagMemberCommand(FlagPrivate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        try {
            if(args.length == 1) {
                String playerId = commandSender.getName();
                String invitedPlayerId = plugin.playersDB.GetPlayerId(args[0]);
                String flagId = plugin.playersDB.GetPlayerFlag(playerId);

                plugin.playersDB.RemovePlayerFlag(invitedPlayerId);

                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }
}
