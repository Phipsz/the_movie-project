package com.offensand.movie.objects;

public class Actor {

  private int    ID;
  private String name;
  private String mail;
  private String phone;
  private String cellphone;

  public Actor(String name, String mail, String phone, String cellphone) {
    this(- 1, name, mail, phone, cellphone);
  }

  public Actor(int ID, String name, String mail, String phone, String cellphone) {
    this.ID = ID;
    this.name = name;
    this.mail = mail;
    this.phone = phone;
    this.cellphone = cellphone;
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

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getCellphone() {
    return cellphone;
  }

  public void setCellphone(String cellphone) {
    this.cellphone = cellphone;
  }

  public void setID(int iD) {
    ID = iD;
  }
}
