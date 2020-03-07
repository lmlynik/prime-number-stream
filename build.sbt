name := "prime-stream"

version := "0.1"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.3"
lazy val akkaHttpVersion = "10.1.11"

lazy val primeServerContract = (project in file("prime-number-server-contract"))
  .enablePlugins(AkkaGrpcPlugin)

lazy val primeNumberServer = (project in file("prime-number-server"))
  .dependsOn(primeServerContract)
  .enablePlugins(JavaAgent)
  .settings(
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime;test",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    )
  )

lazy val proxyService = (project in file("proxy-service"))
  .dependsOn(primeServerContract)
  .enablePlugins(JavaAgent)
  .settings(
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.7" % "runtime;test",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    )
  )