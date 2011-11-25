package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBActorsCharacters {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__actors ("
        + " ID         INTEGER NOT NULL AUTOINCREMENT, "
        + " Name       VARCHAR(50), " + " Mail       VARCHAR(30), "
        + " Phone      VARCHAR(30), " + " Cellphone  VARCHAR(30), "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__actors_characters ("
        + " IDActor      INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDActor, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__characters ("
        + " ID           INTEGER NOT NULL AUTOINCREMENT, "
        + " Name         VARCHAR(50), " + " Description  LONGVARCHAR, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    return false;
  }
}
