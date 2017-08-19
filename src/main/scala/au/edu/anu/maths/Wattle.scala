package au.edu.anu.maths

import java.io.File
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.firefox.FirefoxDriver
import scala.collection.JavaConverters._

trait Wattle extends App {
  private lazy val login_data: (String, String) = {
    if (args.length >= 2) {
      (args(0), args(1))
    } else {
      val passwordFile = new File("wattle.passwd")
      if (passwordFile.exists) {
        val lines = io.Source.fromFile(passwordFile).getLines
        (lines.next, lines.next)
      } else {
        println("provide your username and password as command line arguments")
        System.exit(1)
        ???
      }
    }
  }
  lazy val username = login_data._1
  lazy val password = login_data._2

  System.setProperty("webdriver.gecko.driver", "./geckodriver");

  val profile = new FirefoxProfile()
  profile.setPreference("browser.download.folderList", 2) // download into browser.download.dir
  profile.setPreference("browser.download.dir", new File(".").getAbsolutePath())
  profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "video/mp4")

  val driver = new FirefoxDriver(profile)

  lazy val login = {
    driver.get("https://wattlecourses.anu.edu.au/login/index.php")
    driver.findElementById("username").sendKeys(username)
    driver.findElementById("password").sendKeys(password)
    driver.findElementByCssSelector("input[type='submit']").click
  }

  lazy val courses: Seq[String] = {
    if (args.length >= 3) {
      args.drop(2)
    } else {
      login
      driver.findElementsByCssSelector("div.course_title a").asScala
        .map(_.getAttribute("href").stripPrefix("https://wattlecourses.anu.edu.au/course/view.php?id="))
    }
  }
}