resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.4.2")

//Enable the sbt idea plugin
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")

//Enable the sbt eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.3.0")

// https://bitbucket.org/jmhofer/jacoco4sbt/wiki/Home
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.1")

