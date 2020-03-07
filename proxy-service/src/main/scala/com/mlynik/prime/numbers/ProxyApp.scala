package com.mlynik.prime.numbers

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

object ProxyApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("ProxyApp")
    implicit val executionContext = system.dispatcher

    val clientSettings = GrpcClientSettings.fromConfig(PrimeNumberService.name)

    val client: PrimeNumberService = PrimeNumberServiceClient(clientSettings)

    val route: Route = PrimeRoutes.routes(client)
    Http().bindAndHandle(route, "localhost", 8080)
  }
}
