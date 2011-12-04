package com.offensand.movie.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.offensand.movie.databases.DBConnection;

public class Item {

  private int       ID;
  private String    name;
  private Requisite requisite;
  private Person    intimeOwner;

  public Item(String name, Requisite requisite, Person intimeOwner) {
    this(- 1, name, requisite, intimeOwner);
    if (! saveToDatabase()) {
      System.err.println("An error occured saving " + toString());
    }
  }

  protected Item(int ID, String name, Requisite requisite, Person intimeOwner) {
    this.ID = ID;
    this.name = name;
    this.requisite = requisite;
    this.intimeOwner = intimeOwner;
  }

  public int getID() {
    return ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Requisite getRequisite() {
    return requisite;
  }

  public void setRequisite(Requisite requisite) {
    this.requisite = requisite;
  }

  public Person getIntimeOwner() {
    return intimeOwner;
  }

  public void setIntimeOwner(Person intimeOwner) {
    this.intimeOwner = intimeOwner;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public static Vector<Item> getItems(String[] filterName,
      Requisite[] filterRequisites, Person[] filterIntimeOwner,
      Scene[] filterScene, Set[] filterSet, DBConnection dbConnection) {
    Vector<Item> retVal = new Vector<Item>(0);
    if (! dbConnection.isConnected()) {
      dbConnection.connect();
    }
    String query = "SELECT * FROM " + DBConnection.dbItem;
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
      query += " (Name LIKE '%" + filterName[0] + "%'";
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
      query += " ID IN (SELECT IDItem FROM " + DBConnection.dbReqItem
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
      query += " ID IN (SELECT IDItem FROM " + DBConnection.dbItemChar
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
      query += "ID IN (SELECT IDItem FROM " + DBConnection.dbSetItem
          + " WHERE IDSet IN (SELECT IDSet FROM " + DBConnection.dbSceneSet
          + " WHERE IDScene IN (" + filterScene[0].getID();
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
      query += " ID IN (SELECT IDItem FROM " + DBConnection.dbSetItem
          + " WHERE IDSet IN(" + filterSet[0].getID();
      for (int i = 1; i < filterSet.length; ++i) {
        query += ", " + filterSet[1].getID();
      }
      query += "))";
    }
    try {
      String queryPerson = "SELECT * FROM " + DBConnection.dbChar
          + " WHERE ID=(SELECT IDCharacter FROM " + DBConnection.dbItemChar
          + " WHERE IDItem=?)";
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
      PreparedStatement statPerson = dbConnection.getConnection()
          .prepareStatement(queryPerson);
      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int ID = result.getInt("ID");
        String name = result.getString("name");
        Vector<?> tmp = Requisite.getRequisites(null, null,
            new Item[] { new Item(ID, "", null, null) }, dbConnection);
        Requisite requisite = (Requisite) (tmp.size() >= 1 ? tmp.get(0) : null );
        statPerson.setInt(1, ID);
        ResultSet rs = statPerson.executeQuery();
        Person intimeOwner = null;
        if (rs.next()) {
          int pID = rs.getInt("ID");
          String pName = rs.getString("Name");
          String pDesc = rs.getString("Description");
          tmp = Actor.getActors(new Person[] { new Person(pID, pName, pDesc,
              null) }, null, dbConnection);
          Actor actor = (Actor) (tmp.size() > 1 ? tmp.get(0) : null );
          intimeOwner = new Person(pID, pName, pDesc, actor);
        }
        retVal.add(new Item(ID, name, requisite, intimeOwner));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // TODO Philipp -- control method
    return retVal;
  }

  public boolean saveToDatabase() {
    String query;
    if (ID <= 0) {
      query = "INSERT INTO " + DBConnection.dbItem + "(Name, Description)"
          + " VALUES (?, ?)";
    } else {
      query = "UPDATE " + DBConnection.dbItem
          + " SET Name=?, Description=? WHERE ID=" + ID;
    }
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, name);
      statement.setString(2, "");
      statement.executeUpdate();
      if (ID <= 0) {
        ResultSet set = statement.getGeneratedKeys();
        if ((set != null ) && set.next()) {
          ID = set.getInt(1);
        }
      }
      if (! requisite.saveToDatabase()) {
        System.err.println("an error occured while saving "
            + requisite.toString());
        return false;
      }
      if (! intimeOwner.saveToDatabase()) {
        System.err.println("an error occured saving " + intimeOwner.toString());
        return false;
      }
      if (! DBConnection.saveRelation(requisite, this)) {
        System.err.println("an error occured saving relation between");
        System.err.println(requisite.toString() + " and");
        System.err.println(toString());
        return false;
      }
      if (! DBConnection.saveRelation(this, intimeOwner)) {
        System.err.println("an error occured saving relation between");
        System.err.println(toString() + " and");
        System.err.println(intimeOwner.toString());
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
    String retVal = "Item[ID=" + ID;
    retVal += ", Name=" + name;
    retVal += ", Requisite="
        + (requisite != null ? requisite.getName() : "none" );
    retVal += ", Intime_Owner="
        + (intimeOwner != null ? intimeOwner.getName() : "none" );
    return retVal + "]";
  }
}
