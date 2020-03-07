package com.mlynik.prime.numbers

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.scaladsl.Sink
import com.mlynik.prime.numbers.GetPrimesReply.Result.{Finished, Value}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Span

import scala.concurrent.Await

class PrimeNumberSpec
  extends Matchers
    with WordSpecLike
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit val patience: PatienceConfig = PatienceConfig(5.seconds, Span(100, org.scalatest.time.Millis))

  val serverSystem: ActorSystem = {
    // important to enable HTTP/2 in server ActorSystem's config
    val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val sys = ActorSystem("PrimeNumberServer", conf)
    val bound = new PrimeNuberServer(sys).run()
    // make sure server is bound before using client
    bound.futureValue
    sys
  }

  implicit val clientSystem = ActorSystem("PrimeNumberClient")

  val client = {
    implicit val ec = clientSystem.dispatcher
    PrimeNumberServiceClient(GrpcClientSettings.fromConfig("PrimeNumberService"))
  }

  override def afterAll: Unit = {
    Await.ready(clientSystem.terminate(), 5.seconds)
    Await.ready(serverSystem.terminate(), 5.seconds)
  }

  "PrimeNumberService" should {
    "return stream with prime numbers" in {
      val reply = client.getPrimes(GetPrimesRequest(17)).runWith(Sink.seq)

      val results = reply.futureValue

      results shouldBe Seq(
        GetPrimesReply(Value(3)),
        GetPrimesReply(Value(5)),
        GetPrimesReply(Value(7)),
        GetPrimesReply(Value(11)),
        GetPrimesReply(Value(13)),
        GetPrimesReply(Value(17)),
        GetPrimesReply(Finished(true))
      )
    }
  }
}