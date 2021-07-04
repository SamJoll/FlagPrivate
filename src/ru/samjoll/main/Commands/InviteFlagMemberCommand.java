package ru.samjoll.main.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.samjoll.main.FlagPrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InviteFlagMemberCommand implements CommandExecutor, TabExecutor {
//    Главный класс плагина
    FlagPrivate plugin;

//    Списоки для заполнения
    ArrayList<String> playersList = new ArrayList<>();

//    Конструктор класса
    public InviteFlagMemberCommand(FlagPrivate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        try {
            playersList.clear();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                playersList.add(player.getName());
            }

            if (args.length == 1 && playersList.contains(args[0])) {
                String playerId = plugin.playersDB.GetPlayerId(commandSender.getName());
                String addedPlayerId = plugin.playersDB.GetPlayerId(args[0]);
                String flagId = plugin.playersDB.GetPlayerFlag(playerId);
                String flagPattern = plugin.flagsDB.GetFlagPattern(flagId);

                if(flagId != null) {
//                    plugin.playersDB.SetPlayerFlag(addedPlayerId, flagId, flagPattern);
                    plugin.playersDB.AddNewInvitation(addedPlayerId, flagId);
                    Bukkit.getPlayer(UUID.fromString(addedPlayerId)).sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "messages.send-invitation")));

                    return true;
                } else {
                    Bukkit.getPlayer(UUID.fromString(addedPlayerId)).sendMessage(String.valueOf(plugin.GetLangFileLine("ru", "exceptions.send-invitation")));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {


        if(args.length == 0) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                playersList.add(player.getName());
            }

            return playersList;
        } else {
            return null;
        }
    }
}
