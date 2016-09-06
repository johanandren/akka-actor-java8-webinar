/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class CompletionStageUtils {
  /**
   * Retry an operation returning a completion stage up to maxRetries times in case the
   * completion stage is failed.
   */
  public static <T> CompletionStage<T> withRetries(Supplier<CompletionStage<T>> tFactory, int maxRetries) {
    return tFactory.get().handle((t, failure) -> {
      if (failure == null) {
        return CompletableFuture.completedFuture(t);
      } else if (maxRetries > 0) {
        return withRetries(tFactory, maxRetries - 1);
      } else {
        throw new RuntimeException("Reached max retries", failure);
      }
    }).thenCompose(stageT -> stageT);
  }
}
