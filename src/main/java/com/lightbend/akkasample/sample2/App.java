/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample2;

import akka.actor.ActorSystem;
import com.lightbend.akkasample.StdIn;

/**
 * actor that changes behavior
 */
public class App {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();

    System.out.println("ENTER to terminate");
    StdIn.readLine();
    system.terminate();
  }
}
