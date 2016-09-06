/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample4;

public final class Product {
  public final long id;
  public final String name;
  public final String description;

  public Product(long id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
