package ru.samjoll.main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.samjoll.main.Commands.ConfirmInvitationCommand;
import ru.samjoll.main.Commands.InviteFlagMemberCommand;
import ru.samjoll.main.Commands.PluginCommand;
import ru.samjoll.main.Commands.RemoveFlagMemberCommand;
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
//    Путь до папки с переводами
    public final String pluginLangPath = pluginFolderPath + "lang/";
//    Путь до папки с БД
    public final String dataBasePath = pluginFolderPath + "databases/";

//    БД игроков
    public PlayersDB playersDB;
    public FlagsDB flagsDB;



//    Создание папки плагина
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
//    Создание папки с языками
    void CreatePluginLangFolder() {
        File pluginLangFolder = new File(pluginLangPath);

        if(pluginLangFolder.mkdir()) {
            log.sendMessage("[FlagPrivate] The plugin lang folder is created!");
        }
    }
//    Загрузка языков
    void LoadPluginLangFiles() {
        File ruLangFile = new File(getDataFolder(), "lang/ru.yml");

        FileConfiguration ruLangYml = YamlConfiguration.loadConfiguration(ruLangFile);

        if(!ruLangFile.exists()) saveResource("lang/ru.yml", false);
    }

//    Получение строки из файла с сообщениями
    public Object GetLangFileLine(String langFileName, String containerPath) {
        File langFile = new File(pluginLangPath + langFileName + ".yml");

        try {
            YamlConfiguration langYml = YamlConfiguration.loadConfiguration(langFile);
            return langYml.get(containerPath);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

//    Создание папки с БД
    void CreatePluginDB() {
        File pluginDBFile = new File(dataBasePath);

        if(pluginDBFile.mkdir()) {
            log.sendMessage("[FlagPrivate] Data bases plugin's folder is created!");
        }
    }

//    Перезагрузка конфигурации плагина
    public void ReloadConfig() {
        Bukkit.getPluginManager().disablePlugin(this);
        Bukkit.getPluginManager().getPlugin(this.getName()).reloadConfig();
        Bukkit.resetRecipes();
        Bukkit.getPluginManager().enablePlugin(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        CreatePluginFolder();
        CreatePluginConfig();
        CreatePluginLangFolder();
        LoadPluginLangFiles();
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
        getCommand("inviteflagmember").setExecutor(new InviteFlagMemberCommand(this));
        getCommand("inviteflagmember").setTabCompleter(new InviteFlagMemberCommand(this));
        getCommand("removeflagmember").setExecutor(new RemoveFlagMemberCommand(this));
        getCommand("confirminvitation").setExecutor(new ConfirmInvitationCommand(this));
        getCommand("confirminvitation").setTabCompleter(new ConfirmInvitationCommand(this));

        playersDB = new PlayersDB(this);
        flagsDB = new FlagsDB(this);
    }
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
