name := "prime-stream"

version := "0.1"

scalaVersion := "2.13.1"

lazy val primeServerContract = project in file("prime-number-server-contract")

lazy val primeNumberServer =  project in file("prime-number-server")

lazy val proxyService = project in file("proxy-service")