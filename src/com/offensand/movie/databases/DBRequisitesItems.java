package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBRequisitesItems {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__requisites ("
        + " ID           INTEGER NOT NULL AUTOINCREMENT, "
        + " Name         VARCHAR(50), " + " Description  LONGVARCHAR, "
        + " OwnerID      INTEGER, " + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__requisite_pictures ("
        + " IDRequisite  INTEGER, " + " Src          VARCHAR(256), "
        + " Description  LONGVARCHAR, " + " PRIMARY KEY(IDRequisite, Src))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__requisites_items ("
        + " IDRequisite  INTEGER, " + " IDItem       INTEGER, "
        + " PRIMARY KEY(IDRequisite, IDItem))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__items ("
        + " ID           INTEGER NOT NULL AUTOINCREMENT, "
        + " Name         VARCHAR(50), " + " Description  LONGVARCHAR, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__items_characters ("
        + " IDItem       INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDItem, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    return true;
  }
}
