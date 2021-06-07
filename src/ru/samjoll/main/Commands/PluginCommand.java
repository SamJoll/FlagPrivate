package ru.samjoll.main.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import ru.samjoll.main.FlagPrivate;
import ru.samjoll.main.TabSuggests;

import java.util.Arrays;
import java.util.List;

public class PluginCommand implements CommandExecutor, TabExecutor {

//    Главный класс плагина
    FlagPrivate plugin;

//    Конструктор класса
    public PluginCommand(FlagPrivate plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args[0] == "reload") {
            plugin.reloadConfig();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {

        if(args.length == 1) {
            return Arrays.asList(TabSuggests.tabSuggests.clone());
        }

        return null;
    }
}
