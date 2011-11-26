package com.offensand.movie.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import com.offensand.movie.databases.DBConnection;

public class Scene implements Comparable<Scene> {

  public enum TIME {
    MORNING, MIDDAY, AFTERNOON, EVENING, NIGHT
  };

  private int            ID;
  private String         name;
  private TIME           time;
  private int            position;
  private Vector<Item>   items;
  private Vector<Person> characters;

  protected Scene(int ID, String name, TIME time, int position,
      Vector<Item> items, Vector<Person> characters) {
    this.ID = ID;
    this.name = name;
    this.time = time;
    this.position = position;
    this.items = items;
    this.characters = characters;
  }

  public Scene(String name, TIME time, int position, Vector<Item> items,
      Vector<Person> characters) {
    this(- 1, name, time, position, items, characters);
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

  public TIME getTime() {
    return time;
  }

  public void setTime(TIME time) {
    this.time = time;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public Vector<Item> getItems() {
    return items;
  }

  public void setItems(Vector<Item> items) {
    this.items = items;
  }

  public Vector<Person> getCharacters() {
    return characters;
  }

  public void setCharacters(Vector<Person> characters) {
    this.characters = characters;
  }

  @Override
  public int compareTo(Scene o) {
    return this.position - o.position;
  }

  public static Vector<Scene> getScenes(String[] filterName, TIME[] filterTime,
      Person[] filterCharacters, DBConnection dbConnection) {
    Vector<Scene> retVal = new Vector<Scene>(0);
    String query = "SELECT * FROM " + DBConnection.dbScene;
    boolean hasNameFilter = (filterName != null ) && (filterName.length > 0 );
    boolean hasTimeFilter = (filterTime != null ) && (filterTime.length > 0 );
    boolean hasCharacterFilter = (filterCharacters != null )
        && (filterCharacters.length > 0 );
    if (hasNameFilter || hasTimeFilter || hasCharacterFilter) {
      query += " WHERE ";
    }
    if (hasNameFilter) {
      query += "( Name LIKE '%" + filterName[0] + "%'";
      for (int i = 1; i < filterName.length; ++i) {
        query += "OR Name LIKE '%" + filterName[i] + "%'";
      }
      query += ")";
    }
    if (hasTimeFilter) {
      if (hasNameFilter) {
        query += " AND ";
      }
      query += "(Time=" + filterTime[0].name();
      for (int i = 1; i < filterTime.length; ++i) {
        query += "OR Time=" + filterTime[i].name();
      }
      query += ")";
    }
    if (hasCharacterFilter) {
      if (hasNameFilter || hasTimeFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDScene FROM " + DBConnection.dbSceneSet
          + " WHERE IDSet IN (SELECT IDSet FROM " + DBConnection.dbSetChar
          + " WHERE IDCharacter IN (" + filterCharacters[0].getID();
      for (int i = 1; i < filterCharacters.length; ++i) {
        query += ", " + filterCharacters[i].getID();
      }
      query += ")))";
    }
    try {
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("Name");
        TIME time = TIME.valueOf(result.getString("Time"));
        int position = result.getInt("Position");
        Scene[] dummy = new Scene[] { new Scene(ID, name, time, 0, null, null) };
        Vector<Item> items = Item.getItems(null, null, null, dummy, null,
            dbConnection);
        Vector<Person> characters = Person.getPersons(null, null, dummy, null,
            dbConnection);
        retVal.add(new Scene(ID, name, time, position, items, characters));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    Collections.sort(retVal);
    // TODO control Method
    return retVal;
  }
}
