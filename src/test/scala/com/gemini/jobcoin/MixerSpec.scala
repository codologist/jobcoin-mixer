package com.gemini.jobcoin

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import com.gemini.jobcoin.actors.Mixer
import com.gemini.jobcoin.actors.Mixer.InputAddresses

class MixerTests extends TestKit(ActorSystem("Mixer")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val actorRef = TestActorRef[Mixer]
  val actor = actorRef.underlyingActor

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Mixer actor" should {

    "get a new address" in {
      val inputAddr = InputAddresses(addresses = List("xx1","xx2","xx3"))
      val newAddr = actor.generateNewUserData(inputAddr)
      assert(!newAddr.equals(""))
    }
  }
}