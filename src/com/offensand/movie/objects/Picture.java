package com.offensand.movie.objects;

import java.awt.image.BufferedImage;

public class Picture {

  private int           ID;
  private String        description;
  private BufferedImage picture;

  public Picture(int ID, String description, BufferedImage picture) {
    this.ID = ID;
    this.description = description;
    this.picture = picture;
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
}
