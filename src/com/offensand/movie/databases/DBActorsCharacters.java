package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBActorsCharacters {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__actors ("
        + " ID         INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name       VARCHAR(50), " + " Mail       VARCHAR(30), "
        + " Phone      VARCHAR(30), " + " Cellphone  VARCHAR(30), "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__actors_characters ("
        + " IDActor      INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDActor, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__characters ("
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
    return true;
  }

  public static void printAllRelations() {
    String query = "SELECT * FROM movie__actors_characters";
    Statement statement;
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDActor || IDCharacter");
      while (set.next()) {
        System.out.println(set.getInt("IDActor") + " || "
            + set.getInt("IDCharacter"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
