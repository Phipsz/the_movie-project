package com.offensand.movie.objects;

public class Item {

  private int       ID;
  private String    name;
  private Requisite requisite;
  private Person    intimeOwner;

  public Item(String name, Requisite requisite, Person intimeOwner) {
    this(- 1, name, requisite, intimeOwner);
  }

  public Item(int ID, String name, Requisite requisite, Person intimeOwner) {
    this.ID = ID;
    this.name = name;
    this.requisite = requisite;
    this.intimeOwner = intimeOwner;
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

  public Requisite getRequisite() {
    return requisite;
  }

  public void setRequisite(Requisite requisite) {
    this.requisite = requisite;
  }

  public Person getIntimeOwner() {
    return intimeOwner;
  }

  public void setIntimeOwner(Person intimeOwner) {
    this.intimeOwner = intimeOwner;
  }

  public void setID(int iD) {
    ID = iD;
  }
}
