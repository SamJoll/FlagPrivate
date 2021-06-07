package ru.samjoll.main.DataBases;

import ru.samjoll.main.FlagPrivate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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

        dbFlow.close();
        dbCon.close();
    }

//    Запись игрока в таблицу
    public void WritePlayer(String playerName, String playerId) {
        try {
            Connection dbCon = getConnection();
            Statement dbFlow = dbCon.createStatement();

            dbFlow.executeUpdate("INSERT OR IGNORE INTO players VALUES ('" + playerName + "', '" + playerId + "')");

            dbFlow.close();
            dbCon.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

}
