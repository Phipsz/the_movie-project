package com.offensand.movie.objects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.offensand.movie.databases.DBConnection;

public class Picture {

  private int           ID;
  private String        src;
  private String        description;
  private BufferedImage picture;
  private boolean       isRequisitePicture;

  public Picture(Requisite req, String src, String description) {
    BufferedImage picture = null;
    try {
      URL url = new URL(src);
      picture = ImageIO.read(url);
    } catch (IOException ioex) {
    }
    this.src = src;
    this.ID = req.getID();
    this.description = description;
    this.picture = picture;
    isRequisitePicture = true;
  }

  public Picture(Location loc, String src, String description) {
    BufferedImage picture = null;
    try {
      URL url = new URL(src);
      picture = ImageIO.read(url);
    } catch (IOException ioex) {
    }
    this.src = src;
    this.ID = loc.getID();
    this.description = description;
    this.picture = picture;
    isRequisitePicture = false;
  }

  protected Picture(int ID, String description, BufferedImage picture,
      boolean isRequisitePicture) {
    this.ID = ID;
    this.description = description;
    this.picture = picture;
    this.isRequisitePicture = isRequisitePicture;
  }

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BufferedImage getPicture() {
    return picture;
  }

  public void setPicture(BufferedImage picture) {
    this.picture = picture;
  }

  public boolean saveToDatabase() {
    String query = "INSERT INTO ";
    if (isRequisitePicture) {
      query += DBConnection.dbReqPic;
    } else {
      query += DBConnection.dbLocPic;
    }
    query += " (ID, Src, Description)" + "VALUES (?, ?, ?) "
        + "ON DUPLICATE KEY UPDATE "
        + "ID=VALUES(ID), Src=VALUES(Src), Description=VALUES(Description)";
    try {
      PreparedStatement statement = DBConnection.getInstance().getConnection()
          .prepareStatement(query);
      statement.setInt(1, ID);
      statement.setString(2, src);
      statement.setString(3, description);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }
}
