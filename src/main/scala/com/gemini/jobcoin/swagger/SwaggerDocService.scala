package com.gemini.jobcoin.swagger

import com.gemini.jobcoin.actors.{MixerService, TransactionerService}
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.models.auth.BasicAuthDefinition

object SwaggerDocService extends SwaggerHttpService {
  override val apiClasses = Set(classOf[MixerService],classOf[TransactionerService])
  override val host = "localhost:8080"
  override val info = Info(version = "1.0")
  override val externalDocs = None
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
