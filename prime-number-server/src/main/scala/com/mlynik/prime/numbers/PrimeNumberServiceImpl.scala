package com.mlynik.prime.numbers
import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.mlynik.prime.numbers.GetPrimesReply.Result.{Finished, Value}

class PrimeNumberServiceImpl()(implicit mat: Materializer) extends PrimeNumberService {
  //https://gist.githubusercontent.com/mattfowler/62f1be4fbe6d36c0a9d84c94817389ba/raw/468c3e76198db648138902881b7d91dde1994e4d/PrimesStream.scala
  def calculatePrimesStream(end: Int): Stream[Int] = {
    val odds = Stream.from(3, 2).takeWhile(_ <= Math.sqrt(end).toInt)
    val composites = odds.flatMap(i => Stream.from(i * i, 2 * i).takeWhile(_ <= end))
    Stream.from(3, 2).takeWhile(_ <= end).diff(composites)
  }

  override def getPrimes(in: GetPrimesRequest): Source[GetPrimesReply, NotUsed] = {

    Source(calculatePrimesStream(in.value) #::: Stream(-1)).map {
      case -1 => GetPrimesReply(Finished(true))
      case p => GetPrimesReply(Value(p))
    }
  }
}
