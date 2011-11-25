package com.offensand.movie.objects;


public class Person {

  private int    ID;
  private String name;
  private String description;
  private Actor  actor;

  public Person(int ID, String name, String description, Actor actor) {
    this.ID = ID;
    this.name = name;
    this.description = description;
    this.actor = actor;
  }

  public Person(String name, String description, Actor actor) {
    this(- 1, name, description, actor);
  }

  public Person(String name, Actor actor) {
    this(- 1, name, "", actor);
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

  public Actor getActor() {
    return actor;
  }

  public void setActor(Actor actor) {
    this.actor = actor;
  }
}
