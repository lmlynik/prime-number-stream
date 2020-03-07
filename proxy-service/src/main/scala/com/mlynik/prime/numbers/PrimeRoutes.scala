package com.mlynik.prime.numbers

import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mlynik.prime.numbers.GetPrimesReply.Result.{Finished, Value, Error => PrimeError}

object PrimeRoutes {
  def routes(client: PrimeNumberService): Route = {
    pathPrefix("primes") {
      import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
      path(IntNumber) { v =>
        get {
          complete {
            client.getPrimes(GetPrimesRequest(v))
              .map {
                case GetPrimesReply(Value(value)) => ServerSentEvent(value.toString)
                case GetPrimesReply(Finished(_)) => ServerSentEvent("Done")
                case GetPrimesReply(PrimeError(msg)) => ServerSentEvent(msg)
              }
              .recover {
                case e: Exception =>
                  ServerSentEvent(e.getMessage)
              }
          }
        }
      }
    }
  }
}
