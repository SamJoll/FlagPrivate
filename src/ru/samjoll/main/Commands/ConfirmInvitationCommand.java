package ru.samjoll.main.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.samjoll.main.FlagPrivate;

import java.util.ArrayList;
import java.util.List;

public class ConfirmInvitationCommand implements CommandExecutor, TabExecutor {
//    Главный класс плагина
    FlagPrivate plugin;

//    Конструктор класса
    public ConfirmInvitationCommand(FlagPrivate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        try {
            if (args.length == 1) {
                if (plugin.flagsDB.GetFlagIdWithName(args[0]) != null) {

                    String playerId = ((Player) commandSender).getUniqueId().toString();

                    if(plugin.playersDB.GetPlayerFlag(playerId) == null) {

                        final String flagId = plugin.flagsDB.GetFlagIdWithName(args[0]);
                        final String flagPattern = plugin.flagsDB.GetFlagPattern(flagId);

                        plugin.playersDB.ConfirmInvitation(playerId, flagId);
                        plugin.playersDB.SetPlayerFlag(playerId, flagId, flagPattern);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {

        ArrayList<String> flagsNameList = new ArrayList<>();

        try {

            for(String flagId : plugin.playersDB.GetIdListOfFlagInvitation()) {
                flagsNameList.add(plugin.flagsDB.GetFlagNameWithId(flagId));
            }

            if (args.length == 1) {
                return flagsNameList;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }
}
