package com.gemini.jobcoin

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask
import com.gemini.jobcoin.actors.Mixer.{IncomingMoney, InputAddresses, ScheduleTrans}
import com.gemini.jobcoin.actors.{Mixer, Transactioner}
import com.gemini.jobcoin.actors.Transactioner._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.io.StdIn
import scala.language.postfixOps


object JobcoinServer {
  implicit val logEntryFormat = jsonFormat2(TransLogEntry)
  implicit val logFormat = jsonFormat1(TransLog)
  implicit val transactionFormat = jsonFormat3(Transaction)
  implicit val inputFormat = jsonFormat1(InputAddresses)
  implicit val timeout: Timeout = 5.seconds

  def main(args: Array[String]) {

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


    val route =
        post {
          path("/mixer/get-deposit-address")
          entity(as[InputAddresses]){ addr => {
            System.out.println("Input addresses: " + addr.addresses.toString)
              val genAddr: Future[String] = ask(mixer, addr).mapTo[String]
              complete(genAddr)
            }
          }
        } ~
        post {
          path("/trans/deposit-money")
          entity(as[Transaction]){ trans => {
            mixer ! IncomingMoney(trans.to,trans.amount)
            complete(HttpEntity(ContentTypes.`application/json`,"{success}"))
          }
          }
        } ~
        {
          path("")
          complete(helpText)
        } ~
        get {
          path("/trans/get-house-log")
          val logs: Future[TransLog] = ask(transactioner,GetHouseTransLog).mapTo[TransLog]
          complete(logs)
        } ~
        {
          path("/trans/get-user-log")
          val logs: Future[TransLog] = ask(transactioner,GetUserTransLog).mapTo[TransLog]
          complete(logs)
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}