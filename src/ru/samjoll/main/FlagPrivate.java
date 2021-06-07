package ru.samjoll.main;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.samjoll.main.Commands.PluginCommand;
import ru.samjoll.main.CustomItems.Flag;
import ru.samjoll.main.CustomItems.FlagCloth;
import ru.samjoll.main.CustomItems.FlagPillar;
import ru.samjoll.main.DataBases.FlagsDB;
import ru.samjoll.main.DataBases.PlayersDB;
import ru.samjoll.main.Handlers.PlayerHandler;

import java.io.File;

public class FlagPrivate extends JavaPlugin {

//    Консоль сервера Minecraft
    ConsoleCommandSender log = getServer().getConsoleSender();

//    Путь до папки плагина
    final String pluginFolderPath = "plugins/FlagPrivate/";
//    Путь до конфигурации плагина
    final String pluginConfigPath = pluginFolderPath + "config.yml";
//    Путь до папки с БД
    public final String dataBasePath = pluginFolderPath + "databases/";

//    БД игроков
    public PlayersDB playersDB;
    public FlagsDB flagsDB;

//    Создание папки файла
    void CreatePluginFolder() {
        File pluginFolder = new File(pluginFolderPath);

        if(pluginFolder.mkdir()) {
            log.sendMessage("[FlagPrivate] Plugin's folder is created!");
        }
    }
//    Создание конфига
    void CreatePluginConfig() {
        File pluginConfigFile = new File(pluginConfigPath);

        if(!pluginConfigFile.exists()) {
            FileConfiguration pluginConfig = YamlConfiguration.loadConfiguration(pluginConfigFile);

            pluginConfig.options().copyDefaults(true);

            saveDefaultConfig();

            log.sendMessage("[FlagPrivate] Plugin's config is created!");
        }
    }
//    Создание папки с БД
    void CreatePluginDB() {
        File pluginDBFile = new File(dataBasePath);

        if(pluginDBFile.mkdir()) {
            log.sendMessage("[FlagPrivate] Data bases plugin's folder is created!");
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        CreatePluginFolder();
        CreatePluginConfig();
        CreatePluginDB();

        getServer().addRecipe(new FlagPillar(this).getRecipe());
        getServer().addRecipe(new FlagCloth(this).getRecipe());
        getServer().addRecipe(new Flag(this).getRecipe());

        getServer().getPluginManager().registerEvents(new PlayerHandler(this), this);
        getServer().getPluginManager().registerEvents(new FlagPillar(this), this);
        getServer().getPluginManager().registerEvents(new FlagCloth(this), this);
        getServer().getPluginManager().registerEvents(new Flag(this), this);

        getCommand("flagprivate").setExecutor(new PluginCommand(this));
        getCommand("flagprivate").setTabCompleter(new PluginCommand(this));

        playersDB = new PlayersDB(this);
        flagsDB = new FlagsDB(this);
    }
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
