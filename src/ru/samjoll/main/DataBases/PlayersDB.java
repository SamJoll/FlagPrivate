package ru.samjoll.main.DataBases;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import ru.samjoll.main.FlagPrivate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PlayersDB {
//    Главный класс плагина
    FlagPrivate plugin;

//    Путь до БД
    final String playersDataBasePath;

//    Конструктор класса
    public PlayersDB(FlagPrivate plugin) {
        this.plugin = plugin;

        playersDataBasePath = "jdbc:sqlite:" + plugin.dataBasePath + "players.db";

        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            InitDB();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

//    Соединение с БД
    Connection getConnection() throws Exception {
        return DriverManager.getConnection(playersDataBasePath);
    }

//    Иницилизация БД
    void InitDB() throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS players ('name' TEXT, 'id' TEXT, UNIQUE('name', 'id'))");
        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS playersAffiliation ('id' TEXT, 'flag_id' TEXT, 'flag_pattern' TEXT, UNIQUE('id'))");
        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS playersBlocks ('id' TEXT, 'block_id' TEXT, 'x' INTEGER, 'y' INTEGER, 'z' INTEGER, 'world' TEXT)");
        dbFlow.executeUpdate("CREATE TABLE IF NOT EXISTS playersInvitations ('id' TEXT, 'flag_id' TEXT, 'time' DATETIME, UNIQUE('flag_id'))");

        dbFlow.close();
        dbCon.close();

    }

//    Получение имени игрока
    public String GetPlayerName(String id) throws Exception {
        String playerName = new String();

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet playerData = dbFlow.executeQuery(String.format("SELECT * FROM players WHERE id = '%s'", id));

        playerName = playerData.getString("name");

        dbFlow.close();
        dbCon.close();

        return playerName;
    }
//    Получение идентификатора игрока
    public String GetPlayerId(String name) throws Exception {
        String playerId = new String();

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet playerData = dbFlow.executeQuery(String.format("SELECT * FROM players WHERE name = '%s'", name));

        playerId = playerData.getString("id");

        dbFlow.close();
        dbCon.close();

        return playerId;
    }
//    Получение флага игрока
    public String GetPlayerFlag(String id) throws Exception {
        String flagId = null;

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet playerFlagRecord = dbFlow.executeQuery(String.format("SELECT flag_id FROM playersAffiliation WHERE id = '%s'", id));

        if (!playerFlagRecord.isClosed()) {
            flagId = playerFlagRecord.getString("flag_id");
        }

        dbFlow.close();
        dbCon.close();

        return flagId;
    }

//    Запись игрока в таблицу
    public void WritePlayer(String playerName, String playerId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO players VALUES ('%s', '%s')", playerName, playerId));

        dbFlow.close();
        dbCon.close();
    }
//    Установить флаг игрока
    public void SetPlayerFlag(String playerId, String flagId, String flagPattern) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO playersAffiliation VALUES ('%s', '%s', '%s')", playerId, flagId, flagPattern));

        dbFlow.close();
        dbCon.close();
    }
//    Удаление флага игрока
    public void RemovePlayerFlag(String playerId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("DELETE FROM playersAffiliation WHERE id = '%s'", playerId));

        dbFlow.close();
        dbCon.close();
    }
    public void RemoveFlag(String flagId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("DELETE FROM playersAffiliation WHERE flag_id = '%s'", flagId));

        dbFlow.close();
        dbCon.close();
    }
//    Запись блока, поставленного игроком
    public void SetPlayerBlock(String playerId, String blockId, Location blockLoc) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = blockLoc.getBlockX();
        int y = blockLoc.getBlockY();
        int z = blockLoc.getBlockZ();

        String world = blockLoc.getWorld().getName();

        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO playersBlocks VALUES('%s', '%s', '%s', '%s', '%s', '%s')", playerId, blockId, x, y, z, world));

        dbFlow.close();
        dbCon.close();
    }
//    Проверка блока игрока
    public boolean IsPlacedByPlayerBlock(String playerId, String blockId, Location blockLoc) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = blockLoc.getBlockX();
        int y = blockLoc.getBlockY();
        int z = blockLoc.getBlockZ();

        String world = blockLoc.getWorld().getName();

        ResultSet playerBlockRecord = dbFlow.executeQuery(String.format("SELECT * FROM playersBlocks WHERE id = '%s' AND block_Id = '%s' AND x = '%s' AND y = '%s' AND z = '%s' AND world = '%s'", playerId, blockId, x, y, z, world));

        if (!playerBlockRecord.isClosed()) {
            dbFlow.close();
            dbCon.close();

            return true;
        }

        return false;
    }
//    Удаление блока
    public void RemoveBlock(String blockId, Location blockLoc) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        int x = blockLoc.getBlockX();
        int y = blockLoc.getBlockY();
        int z = blockLoc.getBlockZ();

        String world = blockLoc.getWorld().getName();

        dbFlow.executeUpdate(String.format("DELETE FROM playersBlocks WHERE block_Id = '%s' AND x = '%s' AND y = '%s' AND z = '%s' AND world = '%s'", blockId, x, y, z, world));

        dbFlow.close();
        dbCon.close();
    }
//    Добавление нового приглашения
    public void AddNewInvitation(String playerId, String flagId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();


        dbFlow.executeUpdate(String.format("INSERT OR IGNORE INTO playersInvitations VALUES('%s', '%s', TIME('now'))", playerId, flagId));

        dbFlow.close();
        dbCon.close();
    }
//    Получение списка id флагов
    public ArrayList<String> GetIdListOfFlagInvitation() throws Exception {
        ArrayList<String> flagIdList = new ArrayList<>();

        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        ResultSet idsRecord = dbFlow.executeQuery("SELECT flag_id FROM playersInvitations ORDER BY time DESC");

        while(idsRecord.next()) {
            flagIdList.add(idsRecord.getString("flag_id"));
        }

        dbFlow.close();
        dbCon.close();

        return flagIdList;
    }
//    Подтверждение приглашение
    public void ConfirmInvitation(String playerId, String flagId) throws Exception {
        Connection dbCon = getConnection();
        Statement dbFlow = dbCon.createStatement();

        dbFlow.executeUpdate(String.format("DELETE FROM playersInvitations WHERE flag_id = '%s' AND id = '%s'", flagId, playerId));

        dbFlow.close();
        dbCon.close();
    }
}
