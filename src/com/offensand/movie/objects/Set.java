package com.offensand.movie.objects;

import java.util.Vector;

public class Set {

  private int            ID;
  private Scene          partOf;
  private Location       location;
  private Vector<Item>   items;
  private Vector<Person> characters;

  public Set(Scene partOf, Location location, Vector<Item> items,
      Vector<Person> characters) {
    this(- 1, partOf, location, items, characters);
  }

  public Set(int ID, Scene partOf, Location location, Vector<Item> items,
      Vector<Person> characters) {
    this.ID = ID;
    this.partOf = partOf;
    this.location = location;
    this.items = items;
    this.characters = characters;
  }

  public int getID() {
    return ID;
  }

  public void setID(int iD) {
    ID = iD;
  }

  public Scene getPartOf() {
    return partOf;
  }

  public void setPartOf(Scene partOf) {
    this.partOf = partOf;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Vector<Item> getItems() {
    return items;
  }

  public void setItems(Vector<Item> items) {
    this.items = items;
  }

  public Vector<Person> getCharacters() {
    return characters;
  }

  public void setCharacters(Vector<Person> characters) {
    this.characters = characters;
  }
}
