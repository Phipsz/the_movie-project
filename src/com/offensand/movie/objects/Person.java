package com.offensand.movie.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.offensand.movie.databases.DBConnection;

public class Person {

  private int    ID;
  private String name;
  private String description;
  private Actor  actor;

  protected Person(int ID, String name, String description, Actor actor) {
    this.ID = ID;
    this.name = name;
    this.description = description;
    this.actor = actor;
  }

  public Person(String name, String description, Actor actor) {
    this(- 1, name, description, actor);
    saveToDatabase();
  }

  public Person(String name, Actor actor) {
    this(- 1, name, "", actor);
    saveToDatabase();
  }

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Actor getActor() {
    return actor;
  }

  public void setActor(Actor actor) {
    this.actor = actor;
  }

  public static Vector<Person> getPersons(String[] filterName,
      Actor[] filterActor, Scene[] filterScene, Set[] filterSet,
      DBConnection dbConnection) {
    Vector<Person> retVal = new Vector<Person>(0);
    if (! dbConnection.isConnected()) {
      dbConnection.connect();
    }
    String query = "SELECT * FROM " + DBConnection.dbChar;
    boolean hasNameFilter = (filterName != null ) && (filterName.length > 0 );
    boolean hasActorFilter = (filterActor != null ) && (filterActor.length > 0 );
    boolean hasSceneFilter = (filterScene != null ) && (filterScene.length > 0 );
    boolean hasSetFilter = (filterSet != null ) && (filterSet.length > 0 );
    if (hasNameFilter || hasActorFilter || hasSceneFilter || hasSetFilter) {
      query += " WHERE ";
    }
    if (hasNameFilter) {
      query += " ( Name LIKE '%" + filterName[0] + "%'";
      for (int i = 1; i < filterName.length; ++i) {
        query += " OR Name LIKE '%" + filterName[i] + "%'";
      }
      query += ")";
    }
    if (hasActorFilter) {
      if (hasNameFilter) {
        query += " AND ";
      }
      query += " ID IN (SELECT IDCharacter FROM " + DBConnection.dbActorChar
          + " WHERE IDActor IN (" + filterActor[0].getID();
      for (int i = 1; i < filterActor.length; ++i) {
        query += ", " + filterActor[i].getID();
      }
      query += "))";
    }
    if (hasSceneFilter) {
      if (hasNameFilter || hasActorFilter) {
        query += " AND ";
      }
      query += " ID IN (SELECT IDCharacter FROM " + DBConnection.dbSetChar
          + " WHERE IDSet IN(SELECT IDSet FROM " + DBConnection.dbSceneSet
          + " WHERE IDScene IN (" + filterScene[0].getID();
      for (int i = 1; i < filterScene.length; ++i) {
        query += ", " + filterScene[1].getID();
      }
      query += ")))";
    }
    if (hasSetFilter) {
      if (hasNameFilter || hasActorFilter || hasSceneFilter) {
        query += " AND ";
      }
      query += " ID IN (SELECT IDCharacter FROM " + DBConnection.dbSetChar
          + " WHERE IDSet IN (" + filterSet[0].getID();
      for (int i = 1; i < filterSet.length; ++i) {
        query += ", " + filterSet[1].getID();
      }
      query += "))";
    }
    try {
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("Name");
        String description = result.getString("Description");
        Actor actor = null;
        String queryActor = "SELECT ID, Name, Mail, Phone, Cellphone FROM "
            + DBConnection.dbActor + " WHERE ID IN (SELECT IDActor FROM "
            + DBConnection.dbActorChar + " WHERE IDCharacter=" + ID + ")";
        ResultSet res = dbConnection.getConnection().prepareStatement(
            queryActor).executeQuery();
        if (res.next()) {
          int IDActor = res.getInt("ID");
          String nameActor = res.getString("Name");
          String mail = res.getString("Mail");
          String phone = res.getString("Phone");
          String cellphone = res.getString("Cellphone");
          actor = new Actor(IDActor, nameActor, mail, phone, cellphone);
        }
        retVal.add(new Person(ID, name, description, actor));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO Philipp -- control Method
    return retVal;
  }

  public boolean saveToDatabase() {
    String query;
    if (ID <= 0) {
      query = "INSERT INTO " + DBConnection.dbChar + " (Name, Description) "
          + " VALUES (?, ?)";
    } else {
      query = "UPDATE " + DBConnection.dbChar + " SET Name=?, Description=? "
          + " WHERE ID=" + ID;
    }
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, name);
      statement.setString(2, description);
      statement.executeUpdate();
      if (ID <= 0) {
        ResultSet set = statement.getGeneratedKeys();
        if ((set != null ) && set.next()) {
          ID = set.getInt(1);
        }
      }
      if (! actor.saveToDatabase()) {
        System.err.println("an error occured saving " + actor.toString());
        return false;
      }
      if (! DBConnection.saveRelation(actor, this)) {
        System.err.println("an error occured saving relation between");
        System.err.println(actor.toString() + " and");
        System.err.println(toString());
        return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    String retVal = "Person[ID=" + ID;
    retVal += ", Name=" + name;
    retVal += ", Description=" + description;
    retVal += ", Actor=" + (actor != null ? actor.getName() : "none" );
    return retVal + "]";
  }
}
