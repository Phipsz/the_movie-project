package com.offensand.movie.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBLocationsScenes {

  public static boolean createTables(Connection dbConnection) {
    PreparedStatement statement = null;
    String sCreateQuery = "CREATE TABLE movie__locations ("
        + " ID          INTEGER NOT NULL AUTOINCREMENT, "
        + " Name        VARCHAR(50), " + " Usability   VARCHAR(50), "
        + " Description LONGVARCHAR, " + " Longitude   VARCHAR(30), "
        + " Latitude    VARCHAR(30), " + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__location_pictures ("
        + " IDLocation   INTEGER, " + " Src   VARCHAR(256)"
        + " Description  LONGVARCHAR, PRIMARY KEY(ID, Src))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__sets_locations ("
        + " IDSet   INTEGER, " + " IDLocation    INTEGER, "
        + " PRIMARY KEY(IDSet, IDLocation))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__scenes ("
        + " ID     INTEGER NOT NULL AUTOINCREMENT, " + " Name   VARCHAR(50), "
        + " Time      INTEGER, " + " Position      INTEGER, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__sets ("
        + " ID        INTEGER NOT NULL AUTOINCREMENT, "
        + " Name      VARCHAR(50), " + " Action    LONGVARCHAR, "
        + " PRIMARY KEY(ID))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__set_items (" + " IDSet  INTEGER, "
        + " IDItem   INTEGER, " + " PRIMARY KEY(IDSet, IDItem))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__set_characters ("
        + " IDSet      INTEGER, " + " IDCharacter  INTEGER, "
        + " PRIMARY KEY(IDSet, IDCharacter))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    sCreateQuery = "CREATE TABLE movie__scene_sets ("
        + " IDScene      INTEGER, " + " IDSet  INTEGER, "
        + " PRIMARY KEY(IDScene, IDSet))";
    try {
      statement = dbConnection.prepareStatement(sCreateQuery);
      statement.execute();
    } catch (SQLException ex) {
      return false;
    }
    return true;
  }
}