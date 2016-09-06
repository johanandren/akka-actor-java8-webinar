/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.lightbend.akkasample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SampleTest {

  public static final class PingActor extends AbstractActor {
    {
      receive(ReceiveBuilder
        .matchEquals("ping", ping -> sender().tell("pong", self()))
        .build());
    }
  }


  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    JavaTestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void testPingPongInteraction() {
    new JavaTestKit(system) {{


      new Within(duration("3 seconds")) {
        protected void run() {
          Props props = Props.create(PingActor.class);
          ActorRef actor = system.actorOf(props, "ping-pong");

          actor.tell("ping", getRef());

          expectMsgEquals("pong");

        }
      };


    }};
  }

}
