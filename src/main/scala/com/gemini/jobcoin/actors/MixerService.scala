package com.gemini.jobcoin.actors

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import com.gemini.jobcoin.actors.Mixer.GenerateAddress
import com.gemini.jobcoin.utils.DefaultJsonFormats
import io.swagger.annotations._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext

@Api(value = "/mixer", produces = "application/json")
@Path("/mixer")
class MixerService(mixer: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  @Path("/get-deposit-address")
  @ApiOperation(value = "Get a new deposit address for the provided input addresses", notes = "", nickname = "get-deposit-addr", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "addresses", value = "List of input addresses", required = true, dataType = "InputAddresses", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Here is your new input address. You may send your jobcoin to this address now.", response = classOf[String]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def generateAddress =
    path("get-deposit-address" / Segment) { inputAddresses =>
      post {
        complete { (mixer ? GenerateAddress(inputAddresses)).mapTo[String] }
      }
    }

}
