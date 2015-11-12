package au.edu.anu.maths

import scala.collection.JavaConverters._
import org.openqa.selenium.firefox.FirefoxDriver
import scala.io.StdIn
import java.io.BufferedInputStream
import org.apache.http.client.HttpClient
import org.openqa.selenium.firefox.FirefoxProfile
import java.io.File

/**
 * @author scott
 */
object DownloadEchos extends App {

  //  profile = Selenium::WebDriver::Firefox::Profile.new
  //profile["browser.download.folderList"] = 2
  //profile["browser.download.dir"] = 'C:\\'
  //profile["browser.helperApps.neverAsk.saveToDisk"] = 'application/pdf'

  val id = 14944

  val profile = new FirefoxProfile()
  profile.setPreference("browser.download.folderList", 2)
  profile.setPreference("browser.download.dir", new File(".").getAbsolutePath())
  profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "video/mp4")

  val driver = new FirefoxDriver(profile)

  lazy val password = StdIn.readLine

  //  driver.get("http://wattle.anu.edu.au/")
  //  driver.findElementById("username").sendKeys("u5228111")
  //  driver.findElementById("password").sendKeys(password)
  //  driver.findElementByCssSelector("input[type='submit']").click

  driver.get(s"http://wattlecourses.anu.edu.au/course/view.php?id=$id")
  driver.findElementById("username").sendKeys("u5228111")
  driver.findElementById("password").sendKeys(password)
  driver.findElementByCssSelector("input[type='submit']").click

  driver.get(s"http://wattlecourses.anu.edu.au/blocks/echo360_echocenter/direct_link.php?id=$id")

  driver.get(driver.findElementByCssSelector("iframe").getAttribute("src"))

  while (driver.findElementsByCssSelector("img.thumbnail").asScala.isEmpty) {
    Thread.sleep(500)
  }

  for (img <- driver.findElementsByCssSelector("img.thumbnail").asScala) yield {
    // https://capture.anu.edu.au/echocontent/1544/4/2c924469-6647-4fbc-8efc-04a1da97f18f/synopsis/low/00444300.jpg
    val tag = img.getAttribute("src").split("/")(6)

    driver.get(s"https://capture.anu.edu.au/ess/echo/presentation/$tag/mediacontent.m4v")

    // hang around waiting for media.m4v.part to appear and then disappear, then rename the file, then download the next one ...
    while (!(new File("media.m4v").exists)) {
      println("waiting for download to begin...")
      Thread.sleep(500)
    }
    while ((new File("media.m4v.part").exists)) {
      println("waiting for download to end...")
      Thread.sleep(1000)
    }
    new File("media.m4v").renameTo(new File(s"echo360-$id-$tag.m4v"))
  }

}