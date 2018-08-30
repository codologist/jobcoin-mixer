package com.gemini.jobcoin.actors

import akka.actor.ActorRef
import akka.pattern.ask
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import io.swagger.annotations._

import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import com.gemini.jobcoin.utils.DefaultJsonFormats
import com.gemini.jobcoin.actors.Transactioner._

@Api(value = "/transactioner", produces = "application/json")
@Path("/transactioner")
class TransactionerService(transactionActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)
  implicit val transLogEntry = jsonFormat2(TransLogEntry)
  implicit val greetingFormat = jsonFormat1(TransLog)

  val route = getHouseLog ~ getHouseLog

  @Path("get-house-log")
  @ApiOperation(value = "Get the log of transactions for the house", notes = "", nickname = "house-log", httpMethod = "GET", response = classOf[TransLog])
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHouseLog =
    path("transactioner" / "get-house-log") {
      get {
          complete { (transactionActor ? GetHouseTransLog).mapTo[TransLog] }
      }
    }

  @Path("get-user-log")
  @ApiOperation(value = "Get the log of transactions for the users", notes = "", nickname = "user-log", httpMethod = "GET", response = classOf[TransLog])
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getUserLog =
    path("transactioner" / "get-user-log") {
      get {
        complete { (transactionActor ? GetUserTransLog).mapTo[TransLog] }
      }
    }

}
