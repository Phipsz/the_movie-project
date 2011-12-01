package com.offensand.movie;

import java.io.File;

public class HelperClass {

  public static final String binDirectory  = System.getProperty("user.dir")
                                               + File.separator;
  public static final File   binFolder     = new File(binDirectory);
  public static final String mainDirectory = binFolder.getParent()
                                               + File.separator;
  public static final String fileDirectory = mainDirectory + "files"
                                               + File.separator;
  public static final String dataDirectory = mainDirectory + "data"
                                               + File.separator;
  public static final File   mainFolder    = new File(mainDirectory);
  public static final File   fileFolder    = new File(fileDirectory);
  public static final File   dataFolder    = new File(dataDirectory);

  public static void main(String[] args) {
    dataFolder.mkdirs();
    fileFolder.mkdir();
    new TestClass();
  }
}
