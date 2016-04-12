/*
 * This is the source code of Telegram Bot v. 2.0
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Ruben Bermudez, 10/11/14.
 */
package org.telegram.plugins.echo.database;

/**
 * @author Ruben Bermudez
 * @version 2.0
 * @brief System variables
 * @date 10/11/14
 */
class DatabaseConstants {
    static final String controllerDB = "com.mysql.jdbc.Driver";
    static final String userDB = "<your-own-db-user>";
    private static final String databaseName = "<your-own-db-name>";
    public static final String password = "<your-own-db-password>";
    static final String linkDB = "jdbc:mysql://localhost:3306/" + databaseName + "?useUnicode=true&characterEncoding=UTF-8";
}
