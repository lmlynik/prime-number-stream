package com.mlynik.prime.numbers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}

object PrimeNumberServer {
  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system = ActorSystem("PrimeNumberServer", conf)
    new PrimeNumberServer(system).run()
  }
}

class PrimeNumberServer(system: ActorSystem) {
  def run(): Future[Http.ServerBinding] = {

    implicit val sys: ActorSystem = system
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = sys.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      PrimeNumberServiceHandler(new PrimeNumberServiceImpl())

    val binding = Http().bindAndHandleAsync(
      service,
      interface = "127.0.0.1",
      port = 5000,
      connectionContext = HttpConnectionContext())

    binding.foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }

    binding
  }
}