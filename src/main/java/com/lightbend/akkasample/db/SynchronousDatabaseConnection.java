/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.db;

import com.lightbend.akkasample.model.Product;

import java.util.*;

/**
 * A dummy database that is slow and blocking and occasionally "looses the
 * connection". Not thread-safe, not meant to be shared between actors.
 */
public class SynchronousDatabaseConnection {

  private Map<Long, Product> inMemoryDb;
  private final Random failureRandom = new Random();

  public SynchronousDatabaseConnection() {
    final Map<Long, Product> db = new HashMap<Long, Product>();
    inMemoryDb.put(1L, new Product(1, "Clean Socks", "Socks for both feet, that are clean"));
    inMemoryDb.put(2L, new Product(2, "Hat", "A thing you wear on your head"));
    inMemoryDb.put(3L, new Product(3, "Gloves", "Keeps your hands warm on cold days"));
  }

  public Optional<Product> findProduct(long id) {
    // simulate a slow blocking database
    try {
      Thread.sleep(2000);
    } catch (InterruptedException ex) {
      // nothing to do here as it is dummy code
    }

    /*
    if (failureRandom.nextInt(10) > 8) {
      inMemoryDb = null;
      throw new ConnectionLost();
    }
    */

    return Optional.ofNullable(inMemoryDb.get(id));
  }
}
