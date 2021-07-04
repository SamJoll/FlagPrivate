package ru.samjoll.main.DataBases;

import org.bukkit.Location;
import ru.samjoll.main.FlagPrivate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import org.bukkit.util.Vector;

public class FlagsDB {
//    Главный класс плагина
    FlagPrivate plugin;

//    Путь до БД
    final String flagsDataBasePath;

//    Конструктор класса
    public FlagsDB(FlagPrivate plugin) {
        this.plugin = plugin;

        flagsDataBasePath = "jdbc:sqlite:" + plugin.dataBasePath + "flags.db";

        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            InitDB();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

//    Соединение с БД
    Connection getConnection() throws Exception {
        return DriverManager.getConnection(flagsDataBasePath);
    }

//    Иницилизация БД
    void InitDB() throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS flags ('id' TEXT, 'name' TEXT, 'pattern' TEXT, UNIQUE('pattern'))");
        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS flagsLoc ('id' TEXT, 'x' INTEGER, 'y' INTEGER, 'z' INTEGER, 'world' TEXT, UNIQUE('id'))");
        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS flagStrength ('id' TEXT, 'strength' INTEGER, 'x' INTEGER, 'y' INTEGER, 'z' INTEGER, 'world' TEXT, UNIQUE('id'))");

        dbFlow.close();
        dbCon.close();
    }

//    Запись флага
    public void WriteFlag(String id, String name, String pattern) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlowFlag = dbCon.createStatement();

        dbFlowFlag.executeUpdate(String.format("INSERT OR IGNORE INTO flags VALUES ('%s', '%s', '%s')", id, name, pattern));

        dbFlowFlag.close();
        dbCon.close();
    }

//    Сохранение координат флага
    public void WriteFlagLoc(String id, Vector loc, String world) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO flagsLoc VALUES ('%s', '%s', '%s', '%s', '%s')", id, x, y, z, world));

        dbFlow.close();
        dbCon.close();
    }

//    Запись прочности набалдашника флага
    public void WriteFlagKnobStrength(String id, int strengthValue, Location loc) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        String world = loc.getWorld().getName();

        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO flagStrength VALUES('%s', '%s', '%s', '%s', '%s', '%s')", id, strengthValue, x, y, z, world));

        dbFlow.close();
        dbCon.close();
    }
//    Получение текущей прочности набалдашника флага
    public int GetCurrentFlagKnobStrength(String id) throws Exception {
        int strengthValue = 0;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet flagStrengthRecord = dbFlow.executeQuery(String.format("SELECT strength FROM flagStrength WHERE id = '%s'", id));

        if(!flagStrengthRecord.isClosed()) {
            strengthValue = flagStrengthRecord.getInt("strength");
        }

        dbFlow.close();
        dbCon.close();

        return strengthValue;
    }
//    Изменение прочности флага
    public void ChangeFlagKnobStrength(String id, int strengthValue) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("UPDATE flagStrength SET strength = '%s' WHERE id = '%s'", strengthValue, id));

        dbFlow.close();
        dbCon.close();
    }
//    Получение идентификатора флага с помощью координат набалдашника
    public String GetFlagIdWithKnobLoc(Location knobLoc) throws Exception {
        String flagId = null;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = knobLoc.getBlockX();
        int y = knobLoc.getBlockY();
        int z = knobLoc.getBlockZ();

        String world = knobLoc.getWorld().getName();

        ResultSet flagLoc = dbFlow.executeQuery(String.format("SELECT id FROM flagStrength WHERE x = '%s' AND y = '%s' AND z = '%s' AND world = '%s'", x, y, z, world));

        if(!flagLoc.isClosed()) {
            flagId = flagLoc.getString("id");
        }

        dbFlow.close();
        dbCon.close();

        return flagId;
    }

//    Получение списка флагов
    public ArrayList<String> GetFlagsId() throws Exception {
        ArrayList<String> flagsId = new ArrayList<>();

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet flagsIdRecord = dbFlow.executeQuery("SELECT id FROM flags");

        while (flagsIdRecord.next()) {
            flagsId.add(flagsIdRecord.getString(1));
        }

        dbFlow.close();
        dbCon.close();

        return flagsId;
    }

//    Получение идинтификатора флага с помощью имени флага
    public String GetFlagIdWithName(String flagName) throws Exception {

        String flagId = null;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        flagId = dbFlow.executeQuery(String.format("SELECT id FROM flags WHERE name = '%s'", flagName)).getString("id");

        dbFlow.close();
        dbCon.close();

        return flagId;
    }
//    Получение имени флага с помощью id
    public String GetFlagNameWithId(String flagId) throws Exception {

        String flagName = null;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        flagName = dbFlow.executeQuery(String.format("SELECT name FROM flags WHERE id = '%s'", flagId)).getString("name");

        dbFlow.close();
        dbCon.close();

        return flagName;
    }

//    Получение узора флага
    public String GetFlagPattern(String flagId) throws Exception {

        String flagPattern = null;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet flagPatternRecord = dbFlow.executeQuery(String.format("SELECT pattern FROM flags WHERE id = '%s'", flagId));

        if(!flagPatternRecord.isClosed()) {
            flagPattern = flagPatternRecord.getString("pattern");
        }

        dbFlow.close();
        dbCon.close();

        return flagPattern;
    }

//    Получение координат флага
    public Location GetFlagLoc(String id) throws Exception {
        Location flagLoc;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet flagLocRecord = dbFlow.executeQuery("SELECT * FROM flagsLoc WHERE id = '" + id + "'");

        flagLoc = new Location(plugin.getServer().getWorld(flagLocRecord.getString("world")), flagLocRecord.getInt("x"), 0, flagLocRecord.getInt("z"));

        dbFlow.close();
        dbCon.close();

        return flagLoc;
    }

//    Проверка на существование флага
    public boolean onFlagExists(String pattern) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet flagsRecord = dbFlow.executeQuery("SELECT pattern FROM flags WHERE pattern = '" + pattern + "'");

        if (flagsRecord.isClosed()) {
            dbFlow.close();
            dbCon.close();

            return false;
        } else {
            dbFlow.close();
            dbCon.close();

            return true;
        }
    }

//    Удаление флага
    public void RemoveFlag(String flagId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("DELETE FROM flags WHERE id = '%s'", flagId));
        dbFlow.executeUpdate(String.format("DELETE FROM flagsLoc WHERE id = '%s'", flagId));
        dbFlow.executeUpdate(String.format("DELETE FROM flagStrength WHERE id = '%s'", flagId));

        dbFlow.close();
        dbCon.close();
    }
}
