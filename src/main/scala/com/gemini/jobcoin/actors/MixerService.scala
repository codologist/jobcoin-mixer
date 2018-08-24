package com.gemini.jobcoin.actors

import javax.ws.rs.Path
import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import akka.pattern.ask
import io.swagger.annotations._
import com.gemini.jobcoin.actors.Mixer.{GeneratedAddress, InputAddresses}
import com.gemini.jobcoin.actors.Transactioner.Transaction
import com.gemini.jobcoin.utils.DefaultJsonFormats

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

@Api(value = "/mixer", produces = "application/json")
@Path("/mixer")
class MixerService(mixer: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat1(InputAddresses)
  implicit val responseFormat = jsonFormat1(GeneratedAddress)
  implicit val transRequestFormat = jsonFormat3(Transaction)


  val route = generateAddress ~ depositMoney

  @Path("/get-deposit-address")
  @ApiOperation(value = "Get a new deposit address for the provided input addresses", notes = "", nickname = "get-deposit-addr", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "addresses", value = "List of input addresses", required = true, dataType = "InputAddresses", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Here is your new input address. You may send your jobcoin to this address now.", response = classOf[GeneratedAddress]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def generateAddress =
    path("get-deposit-address") {
      post {
        entity(as[InputAddresses]) { request =>
          complete { (mixer ? request).mapTo[GeneratedAddress] }
        }
      }
    }

  @Path("/deposit-money")
  @ApiOperation(value = "Use this to deposit money to the unique address provided to you", notes = "", nickname = "deposit", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "transaction", value = "Transaction details", required = true, dataType = "Transaction", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def depositMoney =
    path("deposit-money") {
      post {
        entity(as[Transaction]) { request =>
          complete { (mixer ? request).mapTo[GeneratedAddress] }
        }
      }
    }

}
