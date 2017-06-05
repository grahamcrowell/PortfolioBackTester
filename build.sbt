
name := "PortfolioBackTester"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0"
)

//libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % Test
)

//see: http://financequotes-api.com/
//libraryDependencies += "com.yahoofinance-api" % "YahooFinanceAPI" % "3.6.1"


libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.2" % Test
)

mainClass in (Compile, run):=Some("package org.fantastic.AkkaPoc.HttpActorSystemRoot")
