package com.offensand.movie.objects;

public class GPS {

  String longitude;
  String latitude;

  public GPS(String longitude, String latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  @Override
  public String toString() {
    return "GPS[longitude=" + longitude + ", latitude=" + latitude + "]";
  }
}
