package com.offensand.movie.databases;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.offensand.movie.objects.Actor;
import com.offensand.movie.objects.GPS;
import com.offensand.movie.objects.Item;
import com.offensand.movie.objects.Location;
import com.offensand.movie.objects.Person;
import com.offensand.movie.objects.Picture;
import com.offensand.movie.objects.Requisite;
import com.offensand.movie.objects.Scene;
import com.offensand.movie.objects.Set;
import com.offensand.movie.objects.Scene.TIME;

public class DBConnection {

  public DBConnection(boolean connectOnline) {
    if (! connectOnline) {
      this.dbName = "Database_Movie";
      setDBSystemDir();
      dbProperties = loadDBProperties();
      String driverName = dbProperties.getProperty("derby.driver");
      loadDatabaseDriver(driverName);
      if (! dbExists()) {
        createDatabase();
      }
    } else {
      // put here code for connectivity to online-database
    }
  }

  public DBConnection() {
    this(false);
  }

  public boolean isConnected() {
    return isConnected;
  }

  private boolean dbExists() {
    boolean bExists = false;
    String dbLocation = getDatabaseLocation();
    File dbFileDir = new File(dbLocation);
    if (dbFileDir.exists()) {
      bExists = true;
    }
    return bExists;
  }

  private void setDBSystemDir() {
    // decide on the db system directory
    String systemDir = fileDirectory;
    System.setProperty("derby.system.home", systemDir);
    // create the db system directory
    File fileSystemDir = new File(systemDir);
    fileSystemDir.mkdir();
  }

  private void loadDatabaseDriver(String driverName) {
    // load Derby driver
    try {
      Class.forName(driverName);
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  private Properties loadDBProperties() {
    FileReader dbPropReader = null;
    try {
      dbPropReader = new FileReader(dataDirectory + "Configuration.properties");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    dbProperties = new Properties();
    try {
      dbProperties.load(dbPropReader);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return dbProperties;
  }

  private boolean createTables(Connection dbConnection) {
    boolean bCreatedTables = false;
    bCreatedTables = DBLocationsScenes.createTables(dbConnection);
    bCreatedTables &= DBActorsCharacters.createTables(dbConnection);
    bCreatedTables &= DBRequisitesItems.createTables(dbConnection);
    return bCreatedTables;
  }

  private boolean createDatabase() {
    boolean bCreated = false;
    Connection dbConnection = null;
    String dbUrl = getDatabaseUrl();
    dbProperties.put("create", "true");
    try {
      dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
      bCreated = createTables(dbConnection);
    } catch (SQLException ex) {
    }
    dbProperties.remove("create");
    return bCreated;
  }

  public boolean connect() {
    String dbUrl = getDatabaseUrl();
    if (isConnected)
      return true;
    try {
      dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
      isConnected = dbConnection != null;
    } catch (SQLException ex) {
      isConnected = false;
      ex.printStackTrace();
    }
    return isConnected;
  }

  public void disconnect() {
    if (isConnected) {
      String dbUrl = getDatabaseUrl();
      dbProperties.put("shutdown", "true");
      try {
        DriverManager.getConnection(dbUrl, dbProperties);
      } catch (SQLException ex) {
      }
      isConnected = false;
    }
  }

  private String getDatabaseLocation() {
    String dbLocation = System.getProperty("derby.system.home") + "/" + dbName;
    return dbLocation;
  }

  private String getDatabaseUrl() {
    String dbUrl = dbProperties.getProperty("derby.url") + dbName;
    return dbUrl;
  }

  /**
   * This method returns all Requisites stored in the Database
   * 
   * @return A Vector containing all Requisites stored in the Database
   */
  public Vector<Requisite> getRequisites() {
    return getRequisites(null, null, null);
  }

  /**
   * This method returns all Requisites matching the specified filters that my
   * be null.
   * 
   * @param filterDescriptions
   *          Filter Requisites by Strings of which at least one has to be in
   *          the description of the Requisite or one of its pictures
   * @return A Vector containing all Requisites matching the specified filters
   */
  public Vector<Requisite> getRequisites(String[] filterDescriptions) {
    return getRequisites(filterDescriptions, null, null);
  }

  /**
   * This method returns all Requisites matching the specified filters that my
   * be null.
   * 
   * @param filterOwner
   *          Filter Requisites by the specified Actors
   * @return A Vector containing all Requisites matching the specified filters
   */
  public Vector<Requisite> getRequisites(Actor[] filterOwner) {
    return getRequisites(null, filterOwner, null);
  }

  /**
   * This method returns all Requisites matching the specified filters that my
   * be null.
   * 
   * @param filterItem
   *          Filter Requisites by the specified Actors
   * @return A Vector containing all Requisites matching the specified filters
   */
  public Vector<Requisite> getRequisites(Item[] filterItem) {
    return getRequisites(null, null, filterItem);
  }

  /**
   * This method returns all Requisites matching the specified filters that may
   * be null.
   * 
   * @param filterDescriptions
   *          Filter Requisites by Strings of which at least one has to be in
   *          the description of the Requisite or one of its pictures
   * @param filterOwner
   *          Filter Requisites by the specified Actors
   * @param filterItem
   *          Filter Requisites by the specified Items
   * @return A Vector containing all Requisites matching the specified filters
   */
  public Vector<Requisite> getRequisites(String[] filterDescriptions,
      Actor[] filterOwner, Item[] filterItem) {
    Vector<Requisite> retVal = new Vector<Requisite>(0);
    if (! isConnected) {
      connect();
    }
    String query = "SELECT * FROM " + dbReq;
    boolean hasDescrFilter = (filterDescriptions != null )
        && (filterDescriptions.length > 0 );
    boolean hasOwnerFilter = (filterOwner != null ) && (filterOwner.length > 0 );
    boolean hasItemFilter = (filterItem != null ) && (filterItem.length > 0 );
    if (hasDescrFilter || hasOwnerFilter || hasItemFilter) {
      query += " WHERE ";
    }
    if (hasDescrFilter) {
      query += "(Name LIKE '%" + filterDescriptions[0] + "%'";
      query += " OR Description LIKE '%" + filterDescriptions[0] + "%'";
      query += " OR ID IN " + "(SELECT IDRequisite FROM " + dbReqPic
          + " WHERE Description LIKE '%" + filterDescriptions[0] + "%')";
      for (int i = 0; i < filterDescriptions.length; ++i) {
        query += " OR Name LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Description LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR ID IN " + "(SELECT IDRequisite FROM " + dbReqPic
            + " WHERE Description LIKE '%" + filterDescriptions[i] + "%')";
      }
      query += ")";
    }
    if (hasOwnerFilter) {
      if (hasDescrFilter) {
        query += " AND ";
      }
      query += "OwnerID IN (" + filterOwner[0].getID();
      for (int i = 1; i < filterOwner.length; ++i) {
        query += ", " + filterOwner[i].getID();
      }
      query += ")";
    }
    if (hasItemFilter) {
      if (hasDescrFilter || hasOwnerFilter) {
        query += " AND ";
      }
      query += "ID IN SELECT IDRequisite FROM " + dbReqItem
          + " WHERE IDItem IN (" + filterItem[0].getID();
      for (int i = 1; i < filterItem.length; ++i) {
        query += ", " + filterItem[i].getID();
      }
      query += ")";
    }
    String queryPic = "SELECT * FROM " + dbReqPic + " WHERE ID=?";
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      PreparedStatement statementPic = dbConnection.prepareStatement(queryPic);
      ResultSet resultReq = statement.executeQuery();
      while (resultReq.next()) {
        int ID = resultReq.getInt("ID");
        String name = resultReq.getString("Name");
        String description = resultReq.getString("Description");
        int ownerID = resultReq.getInt("OwnerID");
        String ownerName = resultReq.getString("ownerS");
        Vector<Picture> images = new Vector<Picture>(0);
        statementPic.setInt(1, ID);
        ResultSet resultPic = statementPic.executeQuery();
        while (resultPic.next()) {
          String imagePath = resultPic.getString("Src");
          BufferedImage img = null;
          try {
            img = ImageIO.read(new File(imagePath));
          } catch (IOException e) {
          }
          String descrImg = resultPic.getString("Description");
          Picture pic = new Picture(ID, descrImg, img);
          images.add(pic);
        }
        retVal.add(new Requisite(ID, name, description, images, ownerID,
            ownerName));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO control method
    return retVal;
  }

  /**
   * This method returns all Actors stored in the Database
   * 
   * @return A Vector containing all Actors stored in the Database
   */
  public Vector<Actor> getActors() {
    return getActors(null, null);
  }

  /**
   * This method returns all Actors matching the specified filters that may be
   * null
   * 
   * @param filterCharacter
   *          Filter Actors playing at least one of the specified Characters
   * @return A Vector containing all Actors matching the specified filters
   */
  public Vector<Actor> getActors(Person[] filterCharacter) {
    return getActors(filterCharacter, null);
  }

  /**
   * This method returns all Actors matching the specified filters that may be
   * null
   * 
   * @param filterNames
   *          Filter Actors matching at least one of the specified names
   * @return A Vector containing all Actors matching the specified filters
   */
  public Vector<Actor> getActors(String[] filterNames) {
    return getActors(null, filterNames);
  }

  /**
   * This method returns all Actors matching the specified filters that may be
   * null
   * 
   * @param filterCharacter
   *          Filter Actors playing at least one of the specified Characters
   * @param filterNames
   *          Filter Actors matching at least one of the specified names
   * @return A Vector containing all Actors matching the specified filters
   */
  public Vector<Actor> getActors(Person[] filterCharacter, String[] filterNames) {
    Vector<Actor> retVal = new Vector<Actor>(0);
    if (! isConnected) {
      connect();
    }
    String query = "SELECT * FROM " + dbActor;
    boolean hasCharacterFilter = (filterCharacter != null )
        && (filterCharacter.length > 0 );
    boolean hasNameFilter = (filterNames != null ) && (filterNames.length > 0 );
    if (hasCharacterFilter || hasNameFilter) {
      query += " WHERE ";
    }
    if (hasCharacterFilter) {
      query += " ID IN (SELECT IDActor FROM " + dbActorChar
          + " WHERE IDCharacter IN(" + filterCharacter[0].getID();
      for (int i = 1; i < filterCharacter.length; ++i) {
        query += ", " + filterCharacter[i].getID();
      }
      query += "))";
    }
    if (hasNameFilter) {
      if (hasCharacterFilter) {
        query += " AND ";
      }
      query += "(Name LIKE '%" + filterNames[0] + "%'";
      for (int i = 1; i < filterNames.length; ++i) {
        query += " OR Name LIKE '%" + filterNames[i] + "%'";
      }
      query += ")";
    }
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("Name");
        String mail = result.getString("Mail");
        String phone = result.getString("Phone");
        String cellphone = result.getString("Cellphone");
        retVal.add(new Actor(ID, name, mail, phone, cellphone));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO control method
    return retVal;
  }

  /**
   * This method returns all Locations stored in the Database
   * 
   * @return A Vector containing all Locations stored in the Database
   */
  public Vector<Location> getLocations() {
    return getLocations(null, null, 0);
  }

  /**
   * This method returns all Locations matching the specified filters that may
   * be null
   * 
   * @param filterDescriptions
   *          Filter Locations Strings of which at least one has to be in the
   *          description of the Location, the Description of one of its
   *          pictures, the usability or the name
   * @return A Vector containing all Locations matching the specified filters
   */
  public Vector<Location> getLocations(String[] filterDescriptions) {
    return getLocations(filterDescriptions, null, 0);
  }

  /**
   * This method returns all Locations matching the specified filters that may
   * be null
   * 
   * @param start
   *          The Location that is used
   * @param radius
   *          the radius around the used Location
   * @return A Vector containing all Locations matching the specified filters
   */
  public Vector<Location> getLocations(Location start, int radius) {
    return getLocations(null, start, radius);
  }

  /**
   * This method returns all Locations matching the specified filters that may
   * be null
   * 
   * @param filterDescriptions
   *          Filter Locations Strings of which at least one has to be in the
   *          description of the Location, the Description of one of its
   *          pictures, the usability or the name
   * @param start
   *          The Location that is used
   * @param radius
   *          the radius around the used Location
   * @return A Vector containing all Locations matching the specified filters
   */
  public Vector<Location> getLocations(String[] filterDescriptions,
      Location start, int radius) {
    Vector<Location> retVal = new Vector<Location>(0);
    String query = "SELECT * FROM " + dbLoc;
    boolean hasDescriptionFilter = (filterDescriptions != null )
        && (filterDescriptions.length > 0 );
    boolean hasLocationFilter = (start != null ) && (radius > 0 );
    if (hasDescriptionFilter || hasLocationFilter) {
      query += " WHERE ";
    }
    if (hasDescriptionFilter) {
      query += "(Name LIKE '%" + filterDescriptions[0] + "%'";
      query += " OR Usability LIKE '%" + filterDescriptions[0] + "%'";
      query += " OR Description LIKE '%" + filterDescriptions[0] + "%'";
      query += " OR ID IN " + "(SELECT IDLocation FROM " + dbLocPic
          + " WHERE Description LIKE '%" + filterDescriptions[0] + "%')";
      for (int i = 0; i < filterDescriptions.length; ++i) {
        query += " OR Name LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Usability LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Description LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR ID IN " + "(SELECT IDLocation FROM " + dbLocPic
            + " WHERE Description LIKE '%" + filterDescriptions[i] + "%')";
      }
      query += ")";
    }
    if (hasLocationFilter) {
      if (hasDescriptionFilter) {
        query += " AND ";
      }
      // TODO
      query += "ID IN (SELECT ID FROM " + dbLoc + " WHERE ";
      /*
       * cast longitude/latitude to decimal, then to double. then compute sqrt
       * of sum of squares and compare to radius and startlocation
       */
      // TODO
      query += ")";
    }
    // TODO
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String village = result.getString("Name");
        String description = result.getString("Description");
        String usability = result.getString("Usability");
        String longitude = result.getString("Longitude");
        String latitude = result.getString("Latitude");
        GPS coordinates = new GPS(longitude, latitude);
        Vector<Picture> images = new Vector<Picture>(0);
        String query2 = "SELECT Src, Description FROM " + dbLocPic
            + " WHERE IDLocation=" + ID;
        PreparedStatement statement2 = dbConnection.prepareStatement(query2);
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
          Picture pic = new Picture(ID, descriptionPic, picture);
          images.add(pic);
        }
        retVal.add(new Location(ID, village, description, usability,
            coordinates, images));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO control Metthod
    return retVal;
  }

  /**
   * This method returns all Items stored in the Database
   * 
   * @return A Vector containing all Items stored in the Database
   */
  public Vector<Item> getItems() {
    return getItems(null, null, null, null, null);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterName
   *          Filter Items by specified Name
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(String[] filterName) {
    return getItems(filterName, null, null, null, null);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterRequisites
   *          Filter Items by specified referenced Requisites
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(Requisite[] filterRequisites) {
    return getItems(null, filterRequisites, null, null, null);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterIntimeOwner
   *          Filter Items by specified referenced intime Owners
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(Person[] filterIntimeOwner) {
    return getItems(null, null, filterIntimeOwner, null, null);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterScene
   *          Filter Items required by specified Scenes
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(Scene[] filterScene) {
    return getItems(null, null, null, filterScene, null);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterSet
   *          Filter Items required by specified Sets
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(Set[] filterSet) {
    return getItems(null, null, null, null, filterSet);
  }

  /**
   * This method returns all Items matching the specified filters that may be
   * null
   * 
   * @param filterName
   *          Filter Items by specified Name
   * @param filterRequisites
   *          Filter Items by specified referenced Requisites
   * @param filterIntimeOwner
   *          Filter Items by specified referenced intime Owners
   * @param filterScene
   *          Filter Items required by specified Scenes
   * @param filterSet
   *          Filter Items required by specified Sets
   * @return A Vector containing all Items matching the specified filters
   */
  public Vector<Item> getItems(String[] filterName,
      Requisite[] filterRequisites, Person[] filterIntimeOwner,
      Scene[] filterScene, Set[] filterSet) {
    Vector<Item> retVal = new Vector<Item>(0);
    String query = "SELECT * FROM " + dbItem;
    boolean hasNameFilter = (filterName != null ) && (filterName.length > 0 );
    boolean hasRequisiteFilter = (filterRequisites != null )
        && (filterRequisites.length > 0 );
    boolean hasIntimeOwnerFilter = (filterIntimeOwner != null )
        && (filterIntimeOwner.length > 0 );
    boolean hasSceneFilter = (filterScene != null ) && (filterScene.length > 0 );
    boolean hasSetFilter = (filterSet != null ) && (filterSet.length > 0 );
    if (hasNameFilter || hasRequisiteFilter || hasIntimeOwnerFilter
        || hasSceneFilter || hasSetFilter) {
      query += " WHERE ";
    }
    if (hasNameFilter) {
      query += "(Name LIKE '%" + filterName[0] + "%'";
      query += " OR Description LIKE '%" + filterName[0] + "%'";
      for (int i = 1; i < filterName.length; ++i) {
        query += " OR Name LIKE '%" + filterName[i] + "%'";
        query += " OR Description LIKE '%" + filterName[i] + "%'";
      }
      query += ")";
    }
    if (hasRequisiteFilter) {
      if (hasNameFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDItem FROM " + dbReqItem
          + " WHERE IDRequisite IN (" + filterRequisites[0].getID();
      for (int i = 1; i < filterRequisites.length; ++i) {
        query += ", " + filterRequisites[i].getID();
      }
      query += "))";
    }
    if (hasIntimeOwnerFilter) {
      if (hasNameFilter || hasRequisiteFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDItem FROM " + dbItemChar
          + " WHERE IDCharacter IN (" + filterIntimeOwner[0].getID();
      for (int i = 1; i < filterIntimeOwner.length; ++i) {
        query += ", " + filterIntimeOwner[i].getID();
      }
      query += "))";
    }
    if (hasSceneFilter) {
      if (hasNameFilter || hasRequisiteFilter || hasIntimeOwnerFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDItem FROM " + dbSetItem
          + "WHERE IDSet IN(SELECT IDSet FROM " + dbSceneSet
          + "WHERE IDScene IN (" + filterScene[0].getID();
      for (int i = 1; i < filterScene.length; ++i) {
        query += ", " + filterScene[1].getID();
      }
      query += ")))";
    }
    if (hasSetFilter) {
      if (hasNameFilter || hasRequisiteFilter || hasIntimeOwnerFilter
          || hasSceneFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDItem FROM " + dbSetItem + "WHERE IDSet IN("
          + filterSet[0].getID();
      for (int i = 1; i < filterSet.length; ++i) {
        query += ", " + filterSet[1].getID();
      }
      query += "))";
    }
    try {
      String queryPerson = "SELECT * FROM " + dbChar
          + " WHERE ID=(SELECT IDCharacter FROM " + dbItemChar
          + " WHERE IDItem=? LIMIT 1)";
      PreparedStatement statement = dbConnection.prepareStatement(query);
      PreparedStatement statPerson = dbConnection.prepareStatement(queryPerson);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("name");
        Vector<?> tmp = getRequisites(new Item[] { new Item(ID, "", null, null) });
        Requisite requisite = (Requisite) (tmp.size() > 1 ? tmp.get(0) : null );
        statPerson.setInt(1, ID);
        ResultSet rs = statPerson.executeQuery();
        Person intimeOwner = null;
        if (rs.next()) {
          int pID = rs.getInt("ID");
          String pName = rs.getString("Name");
          String pDesc = rs.getString("Description");
          tmp = getActors(new Person[] { new Person(pID, pName, pDesc, null) });
          Actor actor = (Actor) (tmp.size() > 1 ? tmp.get(0) : null );
          intimeOwner = new Person(pID, pName, pDesc, actor);
        }
        retVal.add(new Item(ID, name, requisite, intimeOwner));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO control method
    return retVal;
  }

  /**
   * This method returns all Characters stored in the Database
   * 
   * @return A Vector containing all Persons stored in the Database
   */
  public Vector<Person> getPersons() {
    return getPersons(null, null, null, null);
  }

  /**
   * This method returns all Characters matching the specified filters that may
   * be null
   * 
   * @param filterName
   *          Filter Persons matching at least one of the specified names
   * @return A Vector containing all Persons matching the specified filters
   */
  public Vector<Person> getPersons(String[] filterName) {
    return getPersons(filterName, null, null, null);
  }

  /**
   * This method returns all Characters matching the specified filters that may
   * be null
   * 
   * @param filterActor
   *          Filter Persons played by the specified Actors
   * @return A Vector containing all Persons matching the specified filters
   */
  public Vector<Person> getPersons(Actor[] filterActor) {
    return getPersons(null, filterActor, null, null);
  }

  /**
   * This method returns all Characters matching the specified filters that may
   * be null
   * 
   * @param filterScene
   *          Filter Persons needed in the specified Scenes
   * @return A Vector containing all Persons matching the specified filters
   */
  public Vector<Person> getPersons(Scene[] filterScene) {
    return getPersons(null, null, filterScene, null);
  }

  /**
   * This method returns all Characters matching the specified filters that may
   * be null
   * 
   * @param filterSet
   *          Filter Persons needed in the specified Sets
   * @return A Vector containing all Persons matching the specified filters
   */
  public Vector<Person> getPersons(Set[] filterSet) {
    return getPersons(null, null, null, filterSet);
  }

  /**
   * This method returns all Characters matching the specified filters that may
   * be null
   * 
   * @param filterName
   *          Filter Persons matching at least one of the specified names
   * @param filterActor
   *          Filter Persons played by the specified Actors
   * @param filterScene
   *          Filter Persons needed in the specified Scenes
   * @param filterSet
   *          Filter Persons needed in the specified Sets
   * @return A Vector containing all Persons matching the specified filters
   */
  public Vector<Person> getPersons(String[] filterName, Actor[] filterActor,
      Scene[] filterScene, Set[] filterSet) {
    Vector<Person> retVal = new Vector<Person>(0);
    String query = "SELECT * FROM " + dbChar;
    boolean hasNameFilter = (filterName != null ) && (filterName.length > 0 );
    boolean hasActorFilter = (filterActor != null ) && (filterActor.length > 0 );
    boolean hasSceneFilter = (filterScene != null ) && (filterScene.length > 0 );
    boolean hasSetFilter = (filterSet != null ) && (filterSet.length > 0 );
    if (hasNameFilter || hasActorFilter || hasSceneFilter || hasSetFilter) {
      query += " WHERE ";
    }
    if (hasNameFilter) {
      query += "( Name LIKE '%" + filterName[0] + "%'";
      for (int i = 1; i < filterName.length; ++i) {
        query += "OR Name LIKE '%" + filterName[i] + "%'";
      }
      query += ")";
    }
    if (hasActorFilter) {
      if (hasNameFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDCharacter FROM " + dbActorChar
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
      query += "ID IN (SELECT IDCharacter FROM " + dbSetChar
          + "WHERE IDSet IN(SELECT IDSet FROM " + dbSceneSet
          + "WHERE IDScene IN (" + filterScene[0].getID();
      for (int i = 1; i < filterScene.length; ++i) {
        query += ", " + filterScene[1].getID();
      }
      query += ")))";
    }
    if (hasSetFilter) {
      if (hasNameFilter || hasActorFilter || hasSceneFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDCharacter FROM " + dbSetChar
          + "WHERE IDSet IN(" + filterSet[0].getID();
      for (int i = 1; i < filterSet.length; ++i) {
        query += ", " + filterSet[1].getID();
      }
      query += "))";
    }
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("Name");
        String description = result.getString("Description");
        Actor actor = null;
        String queryActor = "SELECT ID, Name, Mail, Phone, Cellphone FROM "
            + dbActor + " WHERE ID IN (SELECT IDActor FROM " + dbActorChar
            + " WHERE IDCharacter=" + ID + ")";
        ResultSet res = dbConnection.prepareStatement(queryActor)
            .executeQuery();
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
    // TODO control Method
    return retVal;
  }

  /**
   * This method returns all Scenes stored in the Database
   * 
   * @return A Vector containing all Scenes stored in the Database
   */
  public Vector<Scene> getScenes() {
    return getScenes(null, null, null);
  }

  /**
   * This method returns all Scenes matching the specified filters that may be
   * null
   * 
   * @param filterName
   *          Filter Scenes matching at least one of the specified names
   * @return A Vector containing all Scenes matching the specified filters
   */
  public Vector<Scene> getScenes(String[] filterName) {
    return getScenes(filterName, null, null);
  }

  /**
   * This method returns all Scenes matching the specified filters that may be
   * null
   * 
   * @param filterTime
   *          Filter Scenes being played at the specified time
   * @return A Vector containing all Scenes matching the specified filters
   */
  public Vector<Scene> getScenes(TIME[] filterTime) {
    return getScenes(null, filterTime, null);
  }

  /**
   * This method returns all Scenes matching the specified filters that may be
   * null
   * 
   * @param filterCharacters
   *          Filter Scenes using only the specified Characters
   * @return A Vector containing all Scenes matching the specified filters
   */
  public Vector<Scene> getScenes(Person[] filterCharacters) {
    return getScenes(null, null, filterCharacters);
  }

  /**
   * This method returns all Scenes matching the specified filters that may be
   * null
   * 
   * @param filterName
   *          Filter Scenes matching at least one of the specified names
   * @param filterTime
   *          Filter Scenes being played at the specified time
   * @param filterCharacters
   *          Filter Scenes using only the specified Characters
   * @return A Vector containing all Scenes matching the specified filters
   */
  public Vector<Scene> getScenes(String[] filterName, TIME[] filterTime,
      Person[] filterCharacters) {
    Vector<Scene> retVal = new Vector<Scene>(0);
    String query = "SELECT * FROM " + dbScene;
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
      query += "ID IN (SELECT IDScene FROM " + dbSceneSet
          + " WHERE IDSet IN (SELECT IDSet FROM " + dbSetChar
          + " WHERE IDCharacter IN (" + filterCharacters[0].getID();
      for (int i = 1; i < filterCharacters.length; ++i) {
        query += ", " + filterCharacters[i].getID();
      }
      query += ")))";
    }
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("Name");
        TIME time = TIME.valueOf(result.getString("Time"));
        int position = result.getInt("Position");
        Scene[] dummy = new Scene[] { new Scene(ID, name, time, 0, null, null) };
        Vector<Item> items = getItems(dummy);
        Vector<Person> characters = getPersons(dummy);
        retVal.add(new Scene(ID, name, time, position, items, characters));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    Collections.sort(retVal);
    // TODO control Method
    return retVal;
  }

  /**
   * This method returns all Sets stored in the Database
   * 
   * @return A Vector containing all Sets stored in the Database
   */
  public Vector<Set> getSets() {
    return getSets(null, null, null);
  }

  /**
   * This method returns all Sets matching the specified filters that may be
   * null
   * 
   * @param filterScene
   *          Filter Sets being part of the specified Scenes
   * @return A Vector containing all Sets matching the specified filters
   */
  public Vector<Set> getSets(Scene[] filterScene) {
    return getSets(filterScene, null, null);
  }

  /**
   * This method returns all Sets matching the specified filters that may be
   * null
   * 
   * @param filterLocation
   *          Filter Sets located at the specified Loactions
   * @return A Vector containing all Sets matching the specified filters
   */
  public Vector<Set> getSets(Location[] filterLocation) {
    return getSets(null, filterLocation, null);
  }

  /**
   * This method returns all Sets matching the specified filters that may be
   * null
   * 
   * @param filterCharacters
   *          Filter Sets using only the specified Characters
   * @return A Vector containing all Sets matching the specified filters
   */
  public Vector<Set> getSets(Person[] filterCharacters) {
    return getSets(null, null, filterCharacters);
  }

  /**
   * This method returns all Sets matching the specified filters that may be
   * null
   * 
   * @param filterScene
   *          Filter Sets being part of the specified Scenes
   * @param filterLocation
   *          Filter Sets located at the specified Loactions
   * @param filterCharacters
   *          Filter Sets using only the specified Characters
   * @return A Vector containing all Sets matching the specified filters
   */
  public Vector<Set> getSets(Scene[] filterScene, Location[] filterLocation,
      Person[] filterCharacters) {
    Vector<Set> retVal = new Vector<Set>(0);
    String query = "SELECT * FROM " + dbSet;
    boolean hasSceneFilter = (filterScene != null ) && (filterScene.length > 0 );
    boolean hasLocationFilter = (filterLocation != null )
        && (filterLocation.length > 0 );
    boolean hasCharacterFilter = (filterCharacters != null )
        && (filterCharacters.length > 0 );
    if (hasSceneFilter || hasLocationFilter || hasCharacterFilter) {
      query += " WHERE ";
    }
    if (hasSceneFilter) {
      query += "ID IN (SELECT IDSet FROM " + dbSceneSet + " WHERE IDScene IN ("
          + filterScene[0].getID();
      for (int i = 1; i < filterScene.length; ++i) {
        query += ", " + filterScene[i].getID();
      }
      query += "))";
    }
    if (hasLocationFilter) {
      if (hasSceneFilter) {
        query += " AND ";
      }
      query += "ID IN (SELECT IDSet FROM " + dbSetLoc
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
      query += "ID IN (SELECT IDSet FROM " + dbSetChar
          + " WHERE IDCharacter IN (" + filterCharacters[0].getID();
      for (int i = 1; i < filterCharacters.length; ++i) {
        query += ", " + filterCharacters[i].getID();
      }
      query += "))";
    }
    try {
      PreparedStatement statement = dbConnection.prepareStatement(query);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        Scene partOf = null;
        String query2 = "SELECT * FROM " + dbScene
            + " WHERE ID=(SELECT IDScene FROM " + dbSceneSet + " WHERE IDSet="
            + ID + ")";
        PreparedStatement statement2 = dbConnection.prepareStatement(query2);
        ResultSet res = statement2.executeQuery();
        if (res.next()) {
          int IDScene = res.getInt("ID");
          String nameScene = res.getString("Name");
          TIME timeScene = TIME.valueOf(res.getString("Time"));
          int positionScene = res.getInt("Position");
          Scene[] dummyScene = new Scene[] { new Scene(IDScene, nameScene,
              timeScene, 0, null, null) };
          Vector<Item> itemsScene = getItems(dummyScene);
          Vector<Person> charactersScene = getPersons(dummyScene);
          partOf = new Scene(IDScene, nameScene, timeScene, positionScene,
              itemsScene, charactersScene);
        }
        Location location = null;
        query2 = "SELECT * FROM " + dbLoc
            + " WHERE ID=(SELECT IDLocation FROM " + dbSetLoc + " WHERE IDSet="
            + ID + ")";
        statement2 = dbConnection.prepareStatement(query2);
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
          query2 = "SELECT Src, Description FROM " + dbLocPic
              + " WHERE IDLocation=" + IDLocation;
          statement2 = dbConnection.prepareStatement(query2);
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
            Picture pic = new Picture(IDLocation, descriptionPic, picture);
            images.add(pic);
          }
          location = new Location(IDLocation, village, description, usability,
              coordinates, images);
        }
        Set[] dummy = new Set[] { new Set(ID, partOf, location, null, null) };
        Vector<Item> items = getItems(dummy);
        Vector<Person> characters = getPersons(dummy);
        retVal.add(new Set(ID, partOf, location, items, characters));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO control Method
    return retVal;
  }

  public static enum time {
    MORNING, MIDDAY, EVENING, NIGHT, DEFAULT
  };

  private Connection dbConnection;
  private Properties dbProperties;
  private boolean    isConnected;
  private String     dbName;
  private static final String dbLoc = "movie__locations",
      dbLocPic = "movie__location_pictures", dbReq = "movie__requisites",
      dbReqPic = "movie__requisites_pictures",
      dbReqItem = "movie__requisites_items", dbActor = "movie__actors",
      dbActorChar = "movie__actors_characters", dbItem = "movie__items",
      dbItemChar = "movie__items_characters", dbSet = "movie__sets",
      dbSetLoc = "movie__sets_locations", dbSetItem = "movie__set_items",
      dbSetChar = "movie__set_characters", dbScene = "movie__scenes",
      dbSceneSet = "movie__scene_sets", dbChar = "movie__characters";
  private static final String mainDirectory = "" + File.separator,
      fileDirectory = mainDirectory + "files" + File.separator,
      dataDirectory = mainDirectory + "data" + File.separator;
}