package com.mlynik.prime.numbers

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mlynik.prime.numbers.GetPrimesReply.Result.{Finished, Value}

object ProxyApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-system")
    implicit val executionContext = system.dispatcher

    val clientSettings = GrpcClientSettings.fromConfig(PrimeNumberService.name)

    val client: PrimeNumberService = PrimeNumberServiceClient(clientSettings)

    val route: Route = pathPrefix("primes") {
      import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
      path(IntNumber) { v =>
        get {
          complete {
            client.getPrimes(GetPrimesRequest(v))
              .map({
                case GetPrimesReply(Value(value)) => ServerSentEvent(value.toString)
                case GetPrimesReply(Finished(_)) => ServerSentEvent("Done")
              })

          }
        }
      }
    }

    Http().bindAndHandle(route, "localhost", 8080)
  }
}
