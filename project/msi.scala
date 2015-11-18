import sbt._
import Keys._
import com.typesafe.sbt.SbtProguard._
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys

object MSI extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val root = Project(id = "MSI",
    base = file("."),
    settings = buildSettings ++ OneJar.settings)
}

object BuildSettings {
  import Resolvers._
  import Dependencies._

  val buildOrganization = "au.edu.anu.maths"
  val buildVersion = "0.0.1"
  val buildScalaVersion = "2.11.7"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    publishTo := Some(Resolver.sftp("toolkit.tqft.net Maven repository", "tqft.net", "tqft.net/releases") as ("scottmorrison", new java.io.File("/Users/scott/.ssh/id_rsa"))),
    resolvers := sonatypeResolvers ++ tqftResolvers /* ++ SonatypeSettings.publishing */,
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.4" % "test",
    libraryDependencies ++= Seq(junit, slf4j),
    libraryDependencies += httpclient,
    libraryDependencies += selenium.htmlunit,
    libraryDependencies += selenium.firefox,
    EclipseKeys.withSource := true
  )
}

object OneJar {
    import com.github.retronym.SbtOneJar._
    val settings = oneJarSettings ++ Seq(exportJars := true, mainClass in oneJar := Some("au.edu.anu.maths.DownloadEchos"))
}

object Resolvers {
  val sonatypeResolvers = Seq(
    "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases")
  val tqftResolvers = Seq(
	"tqft.net Maven repository" at "https://tqft.net/releases"	
  )
}

object Dependencies {
	val junit = "junit" % "junit" % "4.11" % "test"
	val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.6.1"
	val apfloat = "org.apfloat" % "apfloat" % "1.6.3"		// arbitrary precision integers and floats; much better than BigInt and BigDecimal
	object commons {
		val math = "org.apache.commons" % "commons-math" % "2.2"	// simplex algorithm
		val logging = "commons-logging" % "commons-logging" % "1.1.1"
		val io = "commons-io" % "commons-io" % "2.4"
	}
	object scala {
		val xml = "org.scala-lang.modules" %% "scala-xml" % "1.0.1"
    val parser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"
	}
	val httpclient = "org.apache.httpcomponents" % "httpclient" % "4.3.2"
	val jets3t = "net.java.dev.jets3t" % "jets3t" % "0.9.0"
	val typica = "com.google.code.typica" % "typica" % "1.7.2"
	val guava = "com.google.guava" % "guava" % "16.0.1"
	val findbugs = "com.google.code.findbugs" % "jsr305" % "1.3.9"
	object selenium {
		val firefox = "org.seleniumhq.selenium" % "selenium-firefox-driver" % "2.48.2"
		val htmlunit = "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.48.2"
	}
	object lift {
		val util = "net.liftweb" %% "lift-util" % "2.6-M2"
	}
	val mysql = "mysql" % "mysql-connector-java" % "5.1.24"
	val mapdb = "org.mapdb" % "mapdb" % "0.9.9"
	val slick = "com.typesafe.slick" %% "slick" % "2.1.0"
	val scalaz = "org.scalaz" %% "scalaz-core" % "7.1.0-M6"
	val spire = "org.spire-math" %% "spire" % "0.7.1"
}

