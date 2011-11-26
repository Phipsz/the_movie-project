package com.offensand.movie.objects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.offensand.movie.databases.DBConnection;

public class Location {

  int             ID;
  String          village;
  String          description;
  String          usability;
  GPS             coordinates;
  Vector<Picture> images;

  protected Location(int ID, String village, String description,
      String usability, GPS coordinates, Vector<Picture> images) {
    this.ID = ID;
    this.village = village;
    this.description = description;
    this.usability = usability;
    this.coordinates = coordinates;
    this.images = images;
  }

  public Location(String village, String description, String usability,
      GPS coordinates, Vector<Picture> images) {
    this(- 1, village, description, usability, coordinates, images);
  }

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public String getVillage() {
    return village;
  }

  public void setVillage(String village) {
    this.village = village;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUsability() {
    return usability;
  }

  public void setUsability(String usability) {
    this.usability = usability;
  }

  public GPS getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(GPS coordinates) {
    this.coordinates = coordinates;
  }

  public Vector<Picture> getImages() {
    return images;
  }

  public void setImages(Vector<Picture> images) {
    this.images = images;
  }

  public static Vector<Location> getLocations(String[] filterDescriptions,
      Location start, int radius, DBConnection dbConnection) {
    Vector<Location> retVal = new Vector<Location>(0);
    String query = "SELECT * FROM " + DBConnection.dbLoc;
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
      query += " OR ID IN " + "(SELECT IDLocation FROM "
          + DBConnection.dbLocPic + " WHERE Description LIKE '%"
          + filterDescriptions[0] + "%')";
      for (int i = 0; i < filterDescriptions.length; ++i) {
        query += " OR Name LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Usability LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR Description LIKE '%" + filterDescriptions[i] + "%'";
        query += " OR ID IN " + "(SELECT IDLocation FROM "
            + DBConnection.dbLocPic + " WHERE Description LIKE '%"
            + filterDescriptions[i] + "%')";
      }
      query += ")";
    }
    if (hasLocationFilter) {
      if (hasDescriptionFilter) {
        query += " AND ";
      }
      // TODO
      query += "ID IN (SELECT ID FROM " + DBConnection.dbLoc + " WHERE ";
      /*
       * cast longitude/latitude to decimal, then to double. then compute sqrt
       * of sum of squares and compare to radius and startlocation
       */
      // TODO
      query += ")";
    }
    // TODO
    try {
      PreparedStatement statement = dbConnection.getConnection()
          .prepareStatement(query);
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
        String query2 = "SELECT Src, Description FROM " + DBConnection.dbLocPic
            + " WHERE IDLocation=" + ID;
        PreparedStatement statement2 = dbConnection.getConnection()
            .prepareStatement(query2);
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
    // TODO control Method
    return retVal;
  }
}
