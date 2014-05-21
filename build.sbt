organization  := "com.example"

name := "spray-test"

version       := "0.1"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= {
  val akkaVersion        = "2.3.2"
  val sprayVersion       = "1.3.1"
  val sprayJsonVersion   = "1.2.6"
  Seq(
    "com.typesafe.akka"      %% "akka-actor"                     % akkaVersion,
    "com.typesafe.akka"      %% "akka-slf4j"                     % akkaVersion,
    "io.spray"                % "spray-can"                      % sprayVersion,
    "io.spray"                % "spray-client"                   % sprayVersion,
    "io.spray"                % "spray-http"                     % sprayVersion,
    "io.spray"                % "spray-httpx"                    % sprayVersion,
    "io.spray"                % "spray-io"                       % sprayVersion,
    "io.spray"                % "spray-routing"                  % sprayVersion,
    "io.spray"                % "spray-util"                     % sprayVersion,
    "io.spray"               %% "spray-json"                     % sprayJsonVersion,
    "io.spray"                % "spray-testkit"  % sprayVersion  % "test",
    "com.typesafe.akka"      %%  "akka-actor"    % akkaVersion,
    "com.typesafe.akka"      %%  "akka-testkit"  % akkaVersion   % "test",
    "org.specs2"             %%  "specs2"        % "2.2.3"       % "test",
    "org.scalatest"          %% "scalatest"      % "1.9.1"       % "test"
  )
}

Revolver.settings
