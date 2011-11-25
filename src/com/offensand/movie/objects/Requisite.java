package com.offensand.movie.objects;

import java.util.Vector;

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

  public Requisite(int ID, String name, String description,
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
}
