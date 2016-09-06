/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample.sample4;

import akka.actor.*;
import akka.event.Logging;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import scala.util.Try;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import static com.lightbend.akkasample.sample4.CompletionStageUtils.withRetries;
import static akka.pattern.PatternsCS.*;
import static akka.http.javadsl.server.PathMatchers.*;
import static akka.http.javadsl.server.Directives.*;


public class WebServer extends AbstractLoggingActor {

  private final static Marshaller<Product, RequestEntity> productMarshaller = Jackson.<Product>marshaller();

  // internal protocol
  private static class ServerStarted {
    final String host;
    final int port;
    public ServerStarted(String host, int port) {
      this.host = host;
      this.port = port;
    }
  }
  private static class ServerFailed {
    final Exception cause;
    public ServerFailed(Exception cause) {
      this.cause = cause;
    }
  }

  public static Props props(ActorRef database, String host, int port) {
    return Props.create(WebServer.class, database, host, port);
  }


  private final ActorRef database;
  private final CompletionStage<ServerBinding> bindingCompletionStage;

  public WebServer(ActorRef database, String host, int port) {

    this.database = database;

    final Route route =
      logRequest("request", Logging.InfoLevel(), () ->
        path(segment("products").slash(longSegment()), (productId) ->

          get(() ->
            onComplete(lookupProduct(productId), (Try<DbActor.ProductResult> result) -> {
              if (result.isFailure()) {
                return complete(StatusCodes.SERVICE_UNAVAILABLE);
              } else {
                final DbActor.ProductResult productResult = result.get();
                if (productResult.product.isPresent()) {
                  return completeOK(productResult.product.get(), productMarshaller);
                } else {
                  return complete(StatusCodes.NOT_FOUND);
                }
              }
            })
          )
        )
      );


    Materializer materializer = ActorMaterializer.create(context());
    bindingCompletionStage = Http.get(context().system())
      .bindAndHandle(
        route.flow(context().system(), materializer),
        ConnectHttp.toHost(host, port),
        materializer);


    // starting the http server is async, inform us when it completes, or fails
    pipe(bindingCompletionStage, context().dispatcher()).to(self());

    receive(ReceiveBuilder
      .match(Status.Failure.class, failure -> onFailure(failure.cause()))
      .match(ServerBinding.class, this::onStarted)
      .build());
  }

  private void onStarted(ServerBinding binding) {
    final InetSocketAddress address = binding.localAddress();
    log().info("Server started at {}:{}", address.getHostString(), address.getPort());
  }

  private void onFailure(Throwable cause) {
    log().error(cause, "Failed to start webserver");
    throw new RuntimeException(cause);
  }

  private CompletionStage<DbActor.ProductResult> lookupProduct(long productId) {
    return withRetries(
      () -> ask(
        database, // actor to ask
        new DbActor.GetProduct(productId), // message
        500) // max time in ms to wait before failing
        .thenApply(object -> ((DbActor.ProductResult) object)),
      2 // nr of retries - this means max time a user will have to wait is 2s after which it will always fail
    );
  }


  @Override
  public void postStop() {
    // make sure we stop the http server when actor stops
    bindingCompletionStage.thenAccept(ServerBinding::unbind);
  }
}
