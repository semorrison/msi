package au.edu.anu.maths

import scala.collection.JavaConverters._
import org.openqa.selenium.firefox.FirefoxDriver
import scala.io.StdIn
import java.io.BufferedInputStream
import org.apache.http.client.HttpClient
import org.openqa.selenium.firefox.FirefoxProfile
import java.io.File
import org.openqa.selenium.By
import java.util.Date
import java.util.Calendar
import org.openqa.selenium.Keys
import org.openqa.selenium.Dimension

/**
 * @author scott
 */
object DownloadEchos extends App {

  if (args.length != 2) {
    println("provide your username and course number as command line arguments")
    System.exit(1)
  }
  val username = args(0)
  val id = args(1) // 14944

  val profile = new FirefoxProfile()
  profile.setPreference("browser.download.folderList", 2) // download into browser.download.dir
  profile.setPreference("browser.download.dir", new File(".").getAbsolutePath())
  profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "video/mp4")

  val driver = new FirefoxDriver(profile)

  lazy val password = {
    println("enter password:")
    StdIn.readLine
  }

  driver.get(s"http://wattlecourses.anu.edu.au/course/view.php?id=$id")
  driver.findElementById("username").sendKeys(username)
  driver.findElementById("password").sendKeys(password)
  driver.findElementByCssSelector("input[type='submit']").click

  driver.get(s"http://wattlecourses.anu.edu.au/blocks/echo360_echocenter/direct_link.php?id=$id")

  driver.get(driver.findElementByCssSelector("iframe").getAttribute("src"))

  while (driver.findElementsByCssSelector("div.echo-li-left-wrapper").asScala.isEmpty) {
    Thread.sleep(500)
  }

  val course = driver.findElementById("echo-header-title").getText.split(" ").head

  // https://capture.anu.edu.au:8443/ess/echocenter/index.jsp?sectionId=7695cb54-9541-4553-bca9-fabfc508ef6b
  // https://capture.anu.edu.au:8443/ess/client/api/sections/7695cb54-9541-4553-bca9-fabfc508ef6b/section-data.json?timeZone=Australia/Sydney&pageIndex=1&pageSize=1000&sortOrder=desc&showUnavailable=true&timeZone=Australia/Sydney

  driver.get("https://capture.anu.edu.au:8443/ess/client/api/sections/" + driver.getCurrentUrl.split("=")(1) + "/section-data.json?timeZone=Australia/Sydney&pageIndex=1&pageSize=1000&sortOrder=desc&showUnavailable=true&timeZone=Australia/Sydney")
  val json = driver.getPageSource
  val uuidRegex = """"uuid":"([0-9a-z-]*)"""".r
  val dateRegex = """"startTime":"([0-9-]*)T""".r
  val uuids = uuidRegex.findAllMatchIn(json.drop(json.indexOf("pageContents"))).map(_.group(1)).toList
  val dates = dateRegex.findAllMatchIn(json.drop(json.indexOf("pageContents"))).map(_.group(1)).toList

  for ((uuid, date) <- uuids.zip(dates)) println(uuid + " " + date)

  val year = Calendar.getInstance.get(Calendar.YEAR)

  for ((uuid, date) <- uuids.zip(dates)) yield {

    val filename = course + "_" + date + ".m4v"

    if (!new File(filename).exists) {
      driver.get(s"https://capture.anu.edu.au/ess/echo/presentation/$uuid/mediacontent.m4v")

      // hang around waiting for media.m4v.part to appear and then disappear, then rename the file, then download the next one ...
      while (!(new File("media.m4v").exists)) {
        println("waiting for download to begin...")
        Thread.sleep(500)
      }
      while ((new File("media.m4v.part").exists)) {
        println("waiting for download to end...")
        Thread.sleep(1000)
      }
      new File("media.m4v").renameTo(new File(filename))
    } else {
      println(s"Skipping $filename, already downloaded.")
    }
  }

}