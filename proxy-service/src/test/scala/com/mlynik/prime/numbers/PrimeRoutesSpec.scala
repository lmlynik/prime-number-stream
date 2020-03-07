package com.mlynik.prime.numbers

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Source
import com.mlynik.prime.numbers.GetPrimesReply.Result.Value
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class PrimeRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val service: PrimeNumberService = (_: GetPrimesRequest) => Source.single(GetPrimesReply(Value(1)))

  lazy val routes = PrimeRoutes.routes(service)

  "PrimeRoutes" should {
    "return SSE" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/primes/17")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "data:1\n\n"
      }
    }
  }
}