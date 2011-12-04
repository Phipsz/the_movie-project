package com.offensand.movie.objects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.offensand.movie.databases.DBConnection;
import com.offensand.movie.objects.Scene.TIME;

public class Set {

  private int            ID;
  private String         name;
  private Scene          partOf;
  private String         action;
  private Location       location;
  private Vector<Item>   items;
  private Vector<Person> characters;

  public Set(String name, Scene partOf, Location location, Vector<Item> items,
      Vector<Person> characters) {
    this(- 1, name, partOf, location, items, characters);
    saveToDatabase();
  }

  protected Set(int ID, String name, Scene partOf, Location location,
      Vector<Item> items, Vector<Person> characters) {
    this.ID = ID;
    this.name = name;
    this.partOf = partOf;
    this.location = location;
    this.items = items;
    this.characters = characters;
    action = "";
  }

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public Scene getPartOf() {
    return partOf;
  }

  public void setPartOf(Scene partOf) {
    this.partOf = partOf;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
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

  public void setAction(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }

  public static Vector<Set> getSets(Scene[] filterScene,
      Location[] filterLocation, Person[] filterCharacters,
      DBConnection dbConnection) {
    Vector<Set> retVal = new Vector<Set>(0);
    if (! dbConnection.isConnected()) {
      dbConnection.connect();
    }
    String query = "SELECT * FROM " + DBConnection.dbSet;
    boolean hasSceneFilter = (filterScene != null ) && (filterScene.length > 0 );
    boolean hasLocationFilter = (filterLocation != null )
        && (filterLocation.length > 0 );
    boolean hasCharacterFilter = (filterCharacters != null )
        && (filterCharacters.length > 0 );
    if (hasSceneFilter || hasLocationFilter || hasCharacterFilter) {
      query += " WHERE ";
    }
    if (hasSceneFilter) {
      query += "ID IN (SELECT IDSet FROM " + DBConnection.dbSceneSet
          + " WHERE IDScene IN (" + filterScene[0].getID();
      for (int i = 1; i < filterScene.length; ++i) {
        query += ", " + filterScene[i].getID();
      }
      query += "))";
    }
    if (hasLocationFilter) {
      if (hasSceneFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDSet FROM " + DBConnection.dbSetLoc
          + " WHERE IDLocation IN (" + filterLocation[0].getID();
      for (int i = 1; i < filterLocation.length; ++i) {
        query += ", " + filterLocation[i].getID();
      }
      query += "))";
    }
    if (hasCharacterFilter) {
      if (hasSceneFilter || hasLocationFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDSet FROM " + DBConnection.dbSetChar
          + " WHERE IDCharacter IN (" + filterCharacters[0].getID();
      for (int i = 1; i < filterCharacters.length; ++i) {
        query += ", " + filterCharacters[i].getID();
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
        Scene partOf = null;
        String query2 = "SELECT * FROM " + DBConnection.dbScene
            + " WHERE ID=(SELECT IDScene FROM " + DBConnection.dbSceneSet
            + " WHERE IDSet=" + ID + ")";
        PreparedStatement statement2 = dbConnection.getConnection()
            .prepareStatement(query2);
        ResultSet res = statement2.executeQuery();
        if (res.next()) {
          int IDScene = res.getInt("ID");
          String nameScene = res.getString("Name");
          TIME timeScene = TIME.valueOf(res.getString("Time"));
          int positionScene = res.getInt("Position");
          Scene[] dummyScene = new Scene[] { new Scene(IDScene, nameScene,
              timeScene, 0, null, null) };
          Vector<Item> itemsScene = Item.getItems(null, null, null, dummyScene,
              null, dbConnection);
          Vector<Person> charactersScene = Person.getPersons(null, null,
              dummyScene, null, dbConnection);
          partOf = new Scene(IDScene, nameScene, timeScene, positionScene,
              itemsScene, charactersScene);
        }
        Location location = null;
        query2 = "SELECT * FROM " + DBConnection.dbLoc
            + " WHERE ID=(SELECT IDLocation FROM " + DBConnection.dbSetLoc
            + " WHERE IDSet=" + ID + ")";
        statement2 = dbConnection.getConnection().prepareStatement(query2);
        res = statement2.executeQuery();
        if (res.next()) {
          int IDLocation = res.getInt("ID");
          String village = res.getString("Name");
          String usability = res.getString("Usability");
          String description = res.getString("Description");
          String longitude = res.getString("Longitude");
          String latitude = res.getString("Latitude");
          GPS coordinates = new GPS(longitude, latitude);
          Vector<Picture> images = new Vector<Picture>(0);
          query2 = "SELECT Src, Description FROM " + DBConnection.dbLocPic
              + " WHERE IDLocation=" + IDLocation;
          statement2 = dbConnection.getConnection().prepareStatement(query2);
          ResultSet pics = statement2.executeQuery();
          while (pics.next()) {
            String src = pics.getString("Src");
            String descriptionPic = pics.getString("Description");
            BufferedImage picture = null;
            try {
              URL url = new URL(src);
              picture = ImageIO.read(url);
            } catch (IOException ioex) {
            }
            Picture pic = new Picture(IDLocation, descriptionPic, picture,
                false);
            images.add(pic);
          }
          location = new Location(IDLocation, village, description, usability,
              coordinates, images);
        }
        Set[] dummy = new Set[] { new Set(ID, name, partOf, location, null,
            null) };
        Vector<Item> items = Item.getItems(null, null, null, null, dummy,
            dbConnection);
        Vector<Person> characters = Person.getPersons(null, null, null, dummy,
            dbConnection);
        retVal.add(new Set(ID, name, partOf, location, items, characters));
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
      query = "INSERT INTO " + DBConnection.dbSet + " (Name, Action) "
          + "VALUES (?, ?)";
    } else {
      query = "UPDATE " + DBConnection.dbSet
          + " SET Name=?, Action=? WHERE ID=" + ID;
    }
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, name);
      statement.setString(2, action);
      statement.executeUpdate();
      if (ID <= 0) {
        ResultSet set = statement.getGeneratedKeys();
        if ((set != null ) && set.next()) {
          ID = set.getInt(1);
        }
      }
      if (! partOf.saveToDatabase() && DBConnection.saveRelation(partOf, this))
        return false;
      if (! (location.saveToDatabase() && DBConnection.saveRelation(this,
          location) ))
        return false;
      for (Item item : items) {
        if (! (item.saveToDatabase() && DBConnection.saveRelation(this, item) ))
          return false;
      }
      for (Person person : characters) {
        if (! (person.saveToDatabase() && DBConnection.saveRelation(this,
            person) ))
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
    String retVal = "Set[ID=" + ID;
    retVal += ", Name=" + name;
    retVal += ", Action=" + action;
    retVal += ", Part of Scene " + (partOf != null ? partOf.getName() : "none" );
    retVal += ", Takes place at "
        + (location != null ? location.getVillage() : "none" );
    retVal += ", uses " + items.size() + " items";
    retVal += " and " + characters.size() + " Characters";
    return retVal + "]";
  }
}