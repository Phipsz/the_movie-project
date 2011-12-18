/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.offensand.movie.ui;

import javax.swing.JFrame;

/**
 *
 * @author tvn * 
 */
public class Initialize extends JFrame{
  public Initialize(){
    super("testumgebung");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    add(new ScenenUI());
    setVisible(true);
  }
  public static void main(String[] args){
    Initialize main = new Initialize();
  }
}
