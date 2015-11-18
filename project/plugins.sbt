addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")

// libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11"))

resolvers += Resolver.url("scalasbt",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

// addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.5")


addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-proguard" % "0.2.2")
