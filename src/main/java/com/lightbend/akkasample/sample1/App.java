/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample1;

import com.lightbend.akkasample.StdIn;

/**
 * counter - actor that keeps state
 */
public class App {

  public static void main(String[] args) {


    System.out.println("ENTER to terminate");
    StdIn.readLine();
  }
}
