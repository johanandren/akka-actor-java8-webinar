/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdIn {

  public static String readLine() {
    // written to make it work in intellij as System.console() is null
    // when run inside the IDE
    try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
      return in.readLine();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
