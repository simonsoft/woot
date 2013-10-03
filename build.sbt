name := "woots"

version := "0.0.1"

organization := "com.spiralarm"

scalaVersion := "2.10.3"

seq(webSettings :_*)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/groups/public"

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= {
  val liftVersion = "3.0-SNAPSHOT"
  Seq(
    "net.liftweb"       		%% "lift-webkit"        % liftVersion        	% "compile",
    "org.eclipse.jetty" 	  	%  "jetty-webapp"       % "8.1.7.v20120910"  	% "container,test",
    "org.eclipse.jetty.orbit" 	%  "javax.servlet" 		% "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"    		%  "logback-classic"	% "1.0.6",
    "org.specs2" 				%% "specs2" 			% "2.2.1" 				% "test",
    "org.scalacheck" 			%% "scalacheck" 		% "1.10.1" 				% "test"
  )
}

