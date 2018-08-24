package com.gemini.jobcoin

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.RouteConcatenation
import com.gemini.jobcoin.actors.Mixer.ScheduleTrans
import com.gemini.jobcoin.actors.{Mixer, MixerService, Transactioner, TransactionerService}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.gemini.jobcoin.swagger.SwaggerDocService

import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object JobcoinServer extends App with RouteConcatenation {
  override def main(args: Array[String]) {

    implicit val system = ActorSystem("jobcoin-server")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val transactioner = system.actorOf(Props[Transactioner], "transactioner")
    val mixer = system.actorOf(Props[Mixer], "mixer")

    system.scheduler.schedule(0 seconds, 5 minutes, mixer, ScheduleTrans())

    val helpText: String =
      """
        |Jobcoin Mixer
        |
        |Use the following endpoints to accomplish your tasks
        |
        |Usage:
        |    get-deposit-address(POST) : To get a address to deposit address personal to you. This is a POST operation and needs List of strings which are input addresses
        |    deposit-money(POST): Use this to send money to the address you own. This is a POST with object {to: String, amount: Decimal}"
        |    get-house-log(GET): Use this to get a log of all transactions in the house account. This is a GET
        |    get-user-log(GET): Use this to get a log of all transactions users made. This is a GET
        |
        |
    """.stripMargin

    val routes =
      cors() (new MixerService(mixer).route ~ new TransactionerService(transactioner).route ~ SwaggerDocService.routes)

    val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}