/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample3;

import akka.actor.ActorSystem;
import com.lightbend.akkasample.StdIn;

/**
 * Supervision
 *
 * see more http://doc.akka.io/docs/akka/2.4/general/supervision.html and
 * http://doc.akka.io/docs/akka/2.4.9/java/lambda-fault-tolerance.html
 */
public class App {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();


    System.out.println("ENTER to terminate");
    StdIn.readLine();
    system.terminate();
  }
}
