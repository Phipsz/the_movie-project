package com.offensand.movie;

import java.util.Vector;

import com.offensand.movie.databases.DBConnection;
import com.offensand.movie.objects.Actor;
import com.offensand.movie.objects.GPS;
import com.offensand.movie.objects.Item;
import com.offensand.movie.objects.Location;
import com.offensand.movie.objects.Person;
import com.offensand.movie.objects.Picture;
import com.offensand.movie.objects.Requisite;
import com.offensand.movie.objects.Scene;
import com.offensand.movie.objects.Set;

public class TestClass {

  public TestClass() {
    Actor actorTest = new Actor("actorName", "test@test.de", "0123456789",
        "9876543210");
    Location locationTest = new Location("testVillage", "any Description",
        "used For", new GPS("", ""), new Vector<Picture>(0));
    Person personTest = new Person("personName", actorTest);
    Requisite requisiteTest = new Requisite("requisiteName", "any Description",
        new Vector<Picture>(0), actorTest);
    Requisite requisiteTest2 = new Requisite("anotherRequisite",
        "here comes the Description", new Vector<Picture>(0), actorTest);
    Item itemTest = new Item("itemName", requisiteTest, personTest);
    Item itemTest2 = new Item("aNewItem", requisiteTest2, personTest);
    Scene sceneTest = new Scene("sceneName", Scene.TIME.MORNING, 0,
        new Vector<Item>(0), new Vector<Person>(0));
    Set setTest = new Set("setName", sceneTest, locationTest, new Vector<Item>(
        0), new Vector<Person>(0));
    DBConnection.saveRelation(actorTest, personTest);
    DBConnection.saveRelation(itemTest, personTest);
    DBConnection.saveRelation(requisiteTest, itemTest);
    DBConnection.saveRelation(sceneTest, setTest);
    DBConnection.saveRelation(setTest, itemTest);
    DBConnection.saveRelation(setTest, itemTest2);
    DBConnection.saveRelation(setTest, locationTest);
    DBConnection.saveRelation(setTest, personTest);
    Vector<?> vals = DBConnection.getActors();
    printVector(vals);
    vals = DBConnection.getItems();
    printVector(vals);
    vals = DBConnection.getLocations();
    printVector(vals);
    vals = DBConnection.getPersons();
    printVector(vals);
    vals = DBConnection.getRequisites();
    printVector(vals);
    vals = DBConnection.getScenes();
    printVector(vals);
    vals = DBConnection.getSets();
    printVector(vals);
    // DBActorsCharacters.printAllRelations();
    // DBLocationsScenes.printAllRelations();
    // DBRequisitesItems.printAllRelations();
  }

  private static <T> void printVector(Vector<T> vec) {
    for (T i : vec) {
      System.out.println(i.toString());
    }
  }
}
