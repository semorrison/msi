package au.edu.anu.maths

import java.io.File
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.firefox.FirefoxDriver

trait MathJobs extends App {
  private lazy val login_data: (String, String) = {
    if (args.length >= 2) {
      (args(0), args(1))
    } else {
      val passwordFile = new File("mathjobs.passwd")
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
  profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf")
  profile.setPreference("pdfjs.disabled", true)
  
  val driver = new FirefoxDriver(profile)

  lazy val login = {
    driver.get("https://www.mathjobs.org/jobs/login/ef")
    driver.findElementById("email").sendKeys(username)
    driver.findElementById("pass").sendKeys(password)
    driver.findElementByCssSelector("input[type='submit']").click
  }

  
}