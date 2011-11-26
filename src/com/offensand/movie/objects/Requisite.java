package com.offensand.movie.objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.offensand.movie.databases.DBConnection;

public class Requisite {

  private int             ID;
  private String          name;
  private String          description;
  private Vector<Picture> images;
  private int             ownerID;
  private String          ownerName;

  public Requisite(String name, String description, Vector<Picture> images,
      int ownerID, String ownerName) {
    this(- 1, name, description, images, ownerID, ownerName);
  }

  protected Requisite(int ID, String name, String description,
      Vector<Picture> images, int ownerID, String ownerName) {
    this.ID = ID;
    this.name = name;
    this.description = description;
    this.images = images;
    this.ownerID = ownerID;
    this.ownerName = ownerName;
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

  public Vector<Picture> getImages() {
    return images;
  }

  public void setImages(Vector<Picture> images) {
    this.images = images;
  }

  public int getOwnerID() {
    return ownerID;
  }

  public void setOwnerID(int ownerID) {
    this.ownerID = ownerID;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public static Vector<Requisite> getRequisites(String[] filterDescriptions,
      Actor[] filterOwner, Item[] filterItem, DBConnection dbConnection) {
    Vector<Requisite> retVal = new Vector<Requisite>(0);
    if (! dbConnection.isConnected()) {
      dbConnection.connect();
    }
    String query = "SELECT * FROM " + DBConnection.dbReq;
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
      query += " OR ID IN " + "(SELECT IDRequisite FROM "
          + DBConnection.dbReqPic + " WHERE Description LIKE '%"
          + filterDescriptions[0] + "%')";
      for (int i = 0; i < filterDescriptions.length; ++i) {
        query += " OR Name LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Description LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR ID IN " + "(SELECT IDRequisite FROM "
            + DBConnection.dbReqPic + " WHERE Description LIKE '%"
            + filterDescriptions[i] + "%')";
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
      query += "ID IN SELECT IDRequisite FROM " + DBConnection.dbReqItem
          + " WHERE IDItem IN (" + filterItem[0].getID();
      for (int i = 1; i < filterItem.length; ++i) {
        query += ", " + filterItem[i].getID();
      }
      query += ")";
    }
    String queryPic = "SELECT * FROM " + DBConnection.dbReqPic + " WHERE ID=?";
    try {
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
      PreparedStatement statementPic = dbConnection.getConnection()
          .prepareStatement(queryPic);
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
}
