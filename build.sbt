name := "prime-stream"

version := "0.1"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.3"

lazy val primeNumberServer = (project in file("prime-number-server"))
  .enablePlugins(AkkaGrpcPlugin, JavaAgent)
  .settings(
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime;test",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )

lazy val proxyService = project in file("proxy-service")