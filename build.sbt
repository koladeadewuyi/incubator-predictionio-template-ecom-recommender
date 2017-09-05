name := "template-scala-parallel-ecommercerecommendation"

parallelExecution in Test := false

scalaVersion := "2.11.8"

val pioVersion = "0.12.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % pioVersion % "provided",
  "org.apache.spark"        %% "spark-core"               % "2.0.2" % "provided",
  "org.apache.spark"        %% "spark-mllib"              % "2.0.2" % "provided",
  "com.github.blemale"      %% "scaffeine"                % "2.2.0",
  "net.spy"                 %  "spymemcached"             % "2.12.3",
  "net.debasishg"           %% "redisclient"              % "3.4",
  "org.scalatest"           %% "scalatest"                % "2.2.1" % "test")
