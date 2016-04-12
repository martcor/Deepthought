package org.telegram.plugins.echo.database;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief Strings to create database
 * @date 15 of May of 2015
 */
class CreationStrings {
    public static final int version = 1;
    static final String createVersionTable = "CREATE TABLE IF NOT EXISTS Versions(ID INTEGER PRIMARY KEY AUTO_INCREMENT, Version INTEGER);";
    static final String insertCurrentVersion = "INSERT IGNORE INTO Versions (Version) VALUES(" + version + ");";
    static final String createUsersTable = "CREATE TABLE IF NOT EXISTS Users(" +
            "userId INTEGER NOT NULL, " +
            "userHash BIGINT DEFAULT NULL, " +
            "CONSTRAINT `userPrimaryKey` PRIMARY KEY(userId));";
    static final String createDifferencesDataTable = "create table if not exists DifferencesData (" +
            "botId INTEGER PRIMARY KEY NOT NULL, " +
            "pts INTEGER NOT NULL, " +
            "date INTEGER NOT NULL, " +
            "seq INTEGER NOT NULL);";
    static final String createChatTable = "CREATE TABLE IF NOT EXISTS Chat (" +
            "id INTEGER PRIMARY KEY NOT NULL," +
            "isChannel BOOLEAN NOT NULL DEFAULT FALSE, " +
            "accessHash BIGINT DEFAULT NULL," +
            "flags INTEGER NOT NULL DEFAULT 0," +
            "title VARCHAR(200) NOT NULL," +
            "username VARCHAR(100) DEFAULT NULL," +
            "restrictionReason VARCHAR(500) DEFAULT NULL," +
            "forbidden BOOLEAN NOT NULL DEFAULT FALSE," +
            "migratedTo INTEGER DEFAULT NULL" +
            ");";
}
