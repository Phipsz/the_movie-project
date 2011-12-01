package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBLocationsScenes {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__locations ("
        + " ID          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name        VARCHAR(50), " + " Usability   VARCHAR(50), "
        + " Description LONG VARCHAR, " + " Longitude   VARCHAR(30), "
        + " Latitude    VARCHAR(30), " + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__location_pictures ("
        + " IDLocation   INTEGER, " + " Src   VARCHAR(256), "
        + " Description  LONG VARCHAR, PRIMARY KEY(IDLocation, Src))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__sets_locations ("
        + " IDSet   INTEGER, " + " IDLocation    INTEGER, "
        + " PRIMARY KEY(IDSet, IDLocation))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__scenes ("
        + " ID     INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name   VARCHAR(50), " + " Time      VARCHAR(20), "
        + " Position      INTEGER, " + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__sets ("
        + " ID        INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
        + " Name      VARCHAR(50), " + " Action    LONG VARCHAR, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__set_items (" + " IDSet  INTEGER, "
        + " IDItem   INTEGER, " + " PRIMARY KEY(IDSet, IDItem))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__set_characters ("
        + " IDSet      INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDSet, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__scene_sets ("
        + " IDScene      INTEGER, " + " IDSet  INTEGER, "
        + " PRIMARY KEY(IDScene, IDSet))";
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
    String query = "SELECT * FROM movie__sets_locations";
    Statement statement;
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDSet || IDLocation");
      while (set.next()) {
        System.out.println(set.getInt("IDSet") + " || "
            + set.getInt("IDLocation"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    query = "SELECT * FROM movie__set_items";
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDSet || IDItem");
      while (set.next()) {
        System.out.println(set.getInt("IDSet") + " || " + set.getInt("IDItem"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    query = "SELECT * FROM movie__set_characters";
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDSet || IDCharacter");
      while (set.next()) {
        System.out.println(set.getInt("IDSet") + " || "
            + set.getInt("IDCharacter"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    query = "SELECT * FROM movie__scene_sets";
    try {
      statement = DBConnection.getInstance().getConnection().createStatement();
      ResultSet set = statement.executeQuery(query);
      System.out.println("IDScene || IDSet");
      while (set.next()) {
        System.out
            .println(set.getInt("IDScene") + " || " + set.getInt("IDSet"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}