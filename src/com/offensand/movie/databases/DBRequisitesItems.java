package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBRequisitesItems {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__requisites ("
        + " ID           INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name         VARCHAR(50), " + " Description  LONG VARCHAR, "
        + " Owner      INTEGER, " + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__requisite_pictures ("
        + " IDRequisite  INTEGER, " + " Src          VARCHAR(256), "
        + " Description  LONG VARCHAR, " + " PRIMARY KEY(IDRequisite, Src))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__requisites_items ("
        + " IDRequisite  INTEGER, " + " IDItem       INTEGER, "
        + " PRIMARY KEY(IDRequisite, IDItem))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__items ("
        + " ID           INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name         VARCHAR(50), " + " Description  LONG VARCHAR, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__items_characters ("
        + " IDItem       INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDItem, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  public static void printAllRelations() {
    String query = "SELECT * FROM movie__requisites_items";
    Statement statement;
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDRequisite || IDItem");
      while (set.next()) {
        System.out.println(set.getInt("IDRequisite") + " || "
            + set.getInt("IDItem"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    query = "SELECT * FROM movie__items_characters";
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDItem || IDCharacter");
      while (set.next()) {
        System.out.println(set.getInt("IDItem") + " || "
            + set.getInt("IDCharacter"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
