package com.offensand.movie.objects;

import java.util.Vector;

public class Scene implements Comparable<Scene> {

  public enum TIME {
    MORNING, MIDDAY, AFTERNOON, EVENING, NIGHT
  };

  private int            ID;
  private String         name;
  private TIME           time;
  private int            position;
  private Vector<Item>   items;
  private Vector<Person> characters;

  public Scene(int ID, String name, TIME time, int position,
      Vector<Item> items, Vector<Person> characters) {
    this.ID = ID;
    this.name = name;
    this.time = time;
    this.position = position;
    this.items = items;
    this.characters = characters;
  }

  public Scene(String name, TIME time, int position, Vector<Item> items,
      Vector<Person> characters) {
    this(- 1, name, time, position, items, characters);
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

  public TIME getTime() {
    return time;
  }

  public void setTime(TIME time) {
    this.time = time;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
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

  @Override
  public int compareTo(Scene o) {
    return this.position - o.position;
  }
}
