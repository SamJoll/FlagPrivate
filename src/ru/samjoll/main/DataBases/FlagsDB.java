package ru.samjoll.main.DataBases;

import ru.samjoll.main.FlagPrivate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

        dbFlow.close();
        dbCon.close();
    }

//    Запись флага
    public void WriteFlag(String id, String name, String pattern) {
        try {
            Connection dbCon = getConnection();
            Statement dbFlow = dbCon.createStatement();

            dbFlow.executeUpdate("INSERT OR IGNORE INTO flags VALUES ('" + id + "', '" + name + "', '" + pattern + "')");

            dbFlow.close();
            dbCon.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
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
}
