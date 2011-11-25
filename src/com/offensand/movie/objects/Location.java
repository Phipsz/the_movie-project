package com.offensand.movie.objects;

import java.util.Vector;

public class Location {

  int             ID;
  String          village;
  String          description;
  String          usability;
  GPS             coordinates;
  Vector<Picture> images;

  public Location(int ID, String village, String description, String usability,
      GPS coordinates, Vector<Picture> images) {
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
}
