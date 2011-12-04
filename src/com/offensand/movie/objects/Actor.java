package com.offensand.movie.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.offensand.movie.databases.DBConnection;

public class Actor {

  private int    ID;
  private String name;
  private String mail;
  private String phone;
  private String cellphone;

  public boolean saveToDatabase() {
    String query = "";
    if (ID <= 0) {
      query += "INSERT INTO " + DBConnection.dbActor
          + " (Name, Mail, Phone, Cellphone) VALUES " + "(?, ?, ?, ?)";
    } else {
      query += "UPDATE " + DBConnection.dbActor
          + " SET Name=?, Mail=?, Phone=?, Cellphone=? " + " WHERE ID=" + ID;
    }
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, name);
      statement.setString(2, mail);
      statement.setString(3, phone);
      statement.setString(4, cellphone);
      statement.executeUpdate();
      if (ID <= 0) {
        ResultSet set = statement.getGeneratedKeys();
        if ((set != null ) && set.next()) {
          ID = set.getInt(1);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static Vector<Actor> getActors(Person[] filterCharacter,
      String[] filterNames, DBConnection dbConnection) {
    Vector<Actor> retVal = new Vector<Actor>(0);
    if (! dbConnection.isConnected()) {
      dbConnection.connect();
    }
    String query = "SELECT * FROM " + DBConnection.dbActor;
    boolean hasCharacterFilter = (filterCharacter != null )
        && (filterCharacter.length > 0 );
    boolean hasNameFilter = (filterNames != null ) && (filterNames.length > 0 );
    if (hasCharacterFilter || hasNameFilter) {
      query += " WHERE ";
    }
    if (hasCharacterFilter) {
      query += " ID IN (SELECT IDActor FROM " + DBConnection.dbActorChar
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
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
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
    // TODO Philipp -- control method
    return retVal;
  }

  public Actor(String name, String mail, String phone, String cellphone) {
    this(- 1, name, mail, phone, cellphone);
    saveToDatabase();
  }

  protected Actor(int ID, String name, String mail, String phone,
      String cellphone) {
    this.ID = ID;
    this.name = name;
    this.mail = mail;
    this.phone = phone;
    this.cellphone = cellphone;
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

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCellphone() {
    return cellphone;
  }

  public void setCellphone(String cellphone) {
    this.cellphone = cellphone;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public static Actor getActor(int ID) {
    String query = "SELECT Name, Mail, Phone, Cellphone FROM "
        + DBConnection.dbActor + " WHERE ID=" + ID;
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query);
      ResultSet set = statement.executeQuery();
      if ((set != null ) && set.next()) {
        String name = set.getString("Name");
        String mail = set.getString("Mail");
        String phone = set.getString("Phone");
        String cellphone = set.getString("Cellphone");
        return new Actor(ID, name, mail, phone, cellphone);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    String retVal = "Actor[ID=" + ID;
    retVal += ", Name=" + name;
    retVal += ", Mail=" + mail;
    retVal += ", Phone=" + phone;
    retVal += ", Cellphone" + cellphone;
    return retVal + "]";
  }
}
