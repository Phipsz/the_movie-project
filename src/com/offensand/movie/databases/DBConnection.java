package com.offensand.movie.databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import com.offensand.movie.HelperClass;
import com.offensand.movie.objects.Actor;
import com.offensand.movie.objects.Item;
import com.offensand.movie.objects.Location;
import com.offensand.movie.objects.Person;
import com.offensand.movie.objects.Requisite;
import com.offensand.movie.objects.Scene;
import com.offensand.movie.objects.Set;
import com.offensand.movie.objects.Scene.TIME;

public final class DBConnection {

  public DBConnection(boolean connectOnline) {
    if (! connectOnline) {
      dbName = "Database_Movie";
      setDBSystemDir();
      dbProperties = loadDBProperties();
      String driverName = dbProperties.getProperty("derby.driver");
      loadDatabaseDriver(driverName);
      if (! dbExists()) {
        if (createDatabase()) {
          System.out.print("Creating Database has ");
          System.out.println("been successful");
        } else {
          System.out.print("Creating Database has ");
          System.out.println("failed");
        }
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
    System.out.println(HelperClass.mainDirectory);
    System.out.println(HelperClass.fileDirectory);
    String systemDir = HelperClass.fileDirectory;
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

  private static Properties loadDBProperties() {
    FileReader dbPropReader = null;
    try {
      dbPropReader = new FileReader(HelperClass.dataDirectory
          + "Configuration.properties");
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

  private static boolean createTables(Connection dbConnection) {
    boolean bCreatedTables = false;
    bCreatedTables = DBLocationsScenes.createTables(dbConnection);
    bCreatedTables &= DBActorsCharacters.createTables(dbConnection);
    bCreatedTables &= DBRequisitesItems.createTables(dbConnection);
    return bCreatedTables;
  }

  private static boolean createDatabase() {
    boolean bCreated = false;
    Connection dbConnection = null;
    String dbUrl = getDatabaseUrl();
    dbProperties.put("create", "true");
    try {
      dbConnection = DriverManager.getConnection(dbUrl, dbProperties);
      bCreated = createTables(dbConnection);
    } catch (SQLException ex) {
      ex.printStackTrace();
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

  private static String getDatabaseLocation() {
    String dbLocation = System.getProperty("derby.system.home") + "/" + dbName;
    return dbLocation;
  }

  private static String getDatabaseUrl() {
    String dbUrl = dbProperties.getProperty("derby.url") + dbName;
    return dbUrl;
  }

  /**
   * This method returns all Requisites stored in the Database
   * 
   * @return A Vector containing all Requisites stored in the Database
   */
  public static Vector<Requisite> getRequisites() {
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
  public static Vector<Requisite> getRequisites(String[] filterDescriptions) {
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
  public static Vector<Requisite> getRequisites(Actor[] filterOwner) {
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
  public static Vector<Requisite> getRequisites(Item[] filterItem) {
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
  public static Vector<Requisite> getRequisites(String[] filterDescriptions,
      Actor[] filterOwner, Item[] filterItem) {
    return Requisite.getRequisites(filterDescriptions, filterOwner, filterItem,
        dbConnectionInstance);
  }

  /**
   * This method returns all Actors stored in the Database
   * 
   * @return A Vector containing all Actors stored in the Database
   */
  public static Vector<Actor> getActors() {
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
  public static Vector<Actor> getActors(Person[] filterCharacter) {
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
  public static Vector<Actor> getActors(String[] filterNames) {
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
  public static Vector<Actor> getActors(Person[] filterCharacter,
      String[] filterNames) {
    return Actor.getActors(filterCharacter, filterNames, dbConnectionInstance);
  }

  /**
   * This method returns all Locations stored in the Database
   * 
   * @return A Vector containing all Locations stored in the Database
   */
  public static Vector<Location> getLocations() {
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
  public static Vector<Location> getLocations(String[] filterDescriptions) {
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
  public static Vector<Location> getLocations(Location start, int radius) {
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
  public static Vector<Location> getLocations(String[] filterDescriptions,
      Location start, int radius) {
    return Location.getLocations(filterDescriptions, start, radius,
        dbConnectionInstance);
  }

  /**
   * This method returns all Items stored in the Database
   * 
   * @return A Vector containing all Items stored in the Database
   */
  public static Vector<Item> getItems() {
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
  public static Vector<Item> getItems(String[] filterName) {
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
  public static Vector<Item> getItems(Requisite[] filterRequisites) {
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
  public static Vector<Item> getItems(Person[] filterIntimeOwner) {
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
  public static Vector<Item> getItems(Scene[] filterScene) {
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
  public static Vector<Item> getItems(Set[] filterSet) {
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
  public static Vector<Item> getItems(String[] filterName,
      Requisite[] filterRequisites, Person[] filterIntimeOwner,
      Scene[] filterScene, Set[] filterSet) {
    return Item.getItems(filterName, filterRequisites, filterIntimeOwner,
        filterScene, filterSet, dbConnectionInstance);
  }

  /**
   * This method returns all Characters stored in the Database
   * 
   * @return A Vector containing all Persons stored in the Database
   */
  public static Vector<Person> getPersons() {
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
  public static Vector<Person> getPersons(String[] filterName) {
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
  public static Vector<Person> getPersons(Actor[] filterActor) {
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
  public static Vector<Person> getPersons(Scene[] filterScene) {
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
  public static Vector<Person> getPersons(Set[] filterSet) {
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
  public static Vector<Person> getPersons(String[] filterName,
      Actor[] filterActor, Scene[] filterScene, Set[] filterSet) {
    return Person.getPersons(filterName, filterActor, filterScene, filterSet,
        dbConnectionInstance);
  }

  /**
   * This method returns all Scenes stored in the Database
   * 
   * @return A Vector containing all Scenes stored in the Database
   */
  public static Vector<Scene> getScenes() {
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
  public static Vector<Scene> getScenes(String[] filterName) {
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
  public static Vector<Scene> getScenes(TIME[] filterTime) {
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
  public static Vector<Scene> getScenes(Person[] filterCharacters) {
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
  public static Vector<Scene> getScenes(String[] filterName, TIME[] filterTime,
      Person[] filterCharacters) {
    return Scene.getScenes(filterName, filterTime, filterCharacters,
        dbConnectionInstance);
  }

  /**
   * This method returns all Sets stored in the Database
   * 
   * @return A Vector containing all Sets stored in the Database
   */
  public static Vector<Set> getSets() {
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
  public static Vector<Set> getSets(Scene[] filterScene) {
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
  public static Vector<Set> getSets(Location[] filterLocation) {
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
  public static Vector<Set> getSets(Person[] filterCharacters) {
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
  public static Vector<Set> getSets(Scene[] filterScene,
      Location[] filterLocation, Person[] filterCharacters) {
    return Set.getSets(filterScene, filterLocation, filterCharacters,
        dbConnectionInstance);
  }

  public static DBConnection getInstance() {
    return dbConnectionInstance;
  }

  public static enum time {
    MORNING, MIDDAY, EVENING, NIGHT, DEFAULT
  };

  private Connection          dbConnection;
  private static DBConnection dbConnectionInstance = new DBConnection();
  private static Properties   dbProperties;
  private static boolean      isConnected;
  private static String       dbName;
  public static final String  dbLoc                = "movie__locations",
      dbLocPic = "movie__location_pictures", dbReq = "movie__requisites",
      dbReqPic = "movie__requisite_pictures",
      dbReqItem = "movie__requisites_items", dbActor = "movie__actors",
      dbActorChar = "movie__actors_characters", dbItem = "movie__items",
      dbItemChar = "movie__items_characters", dbSet = "movie__sets",
      dbSetLoc = "movie__sets_locations", dbSetItem = "movie__set_items",
      dbSetChar = "movie__set_characters", dbScene = "movie__scenes",
      dbSceneSet = "movie__scene_sets", dbChar = "movie__characters";

  public Connection getConnection() {
    if (! isConnected) {
      connect();
    }
    return dbConnection;
  }

  private static boolean executeQuery(String query) {
    Statement statement;
    try {
      statement = dbConnectionInstance.dbConnection.createStatement();
      statement.execute(query);
    } catch (SQLException e) {
      if (! (e instanceof SQLIntegrityConstraintViolationException )) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  public static boolean saveRelation(Requisite requisite, Item item) {
    String query = "INSERT INTO " + dbReqItem
        + "(IDRequisite, IDItem) VALUES (" + requisite.getID() + ", "
        + item.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Item item, Person person) {
    String query = "INSERT INTO " + dbItemChar
        + "(IDItem, IDCharacter) VALUES (" + item.getID() + ", "
        + person.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Scene scene, Set set) {
    String query = "INSERT INTO " + dbSceneSet + "(IDScene, IDSet) VALUES ("
        + scene.getID() + ", " + set.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Set set, Item item) {
    String query = "INSERT INTO " + dbSetItem + "(IDSet, IDItem) VALUES ("
        + set.getID() + ", " + item.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Set set, Person person) {
    String query = "INSERT INTO " + dbSetChar + "(IDSet, IDCharacter) VALUES ("
        + set.getID() + ", " + person.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Actor actor, Person person) {
    String query = "INSERT INTO " + dbActorChar
        + "(IDActor, IDCharacter) VALUES (" + actor.getID() + ", "
        + person.getID() + ")";
    return executeQuery(query);
  }

  public static boolean saveRelation(Set set, Location location) {
    String query = "INSERT INTO " + dbSetLoc + "(IDSet, IDLocation) VALUES ("
        + set.getID() + ", " + location.getID() + ")";
    return executeQuery(query);
  }
}