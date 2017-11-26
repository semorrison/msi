package au.edu.anu.maths

import org.openqa.selenium.By
import scala.collection.JavaConverters._
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

object MSIFellows extends MathJobs {
  login

  driver.get("https://www.mathjobs.org/jobs?list-539-0----")
  val candidates = for (e <- driver.findElements(By.cssSelector("a")).asScala; href = e.getAttribute("href"); if href.startsWith("https://www.mathjobs.org/jobs?view-539")) yield {
    (e.getText, href)
  }

  val data = for (c <- candidates) yield {
    println(c._1)
    driver.get(c._2)
    val blob = driver.findElementByCssSelector("body").getText.split("\n").toList
    val name = blob(4).trim
    val currentInstitution = {
      val t = blob.find(_.startsWith("Current Institution")).getOrElse("").stripPrefix("Current Institution ")
      t.take(t.indexOf("Department") - 1).trim
    }
    val advisor = blob.find(_.startsWith("PhD Advisor")).getOrElse("").stripPrefix("PhD Advisor ").trim
    val category = blob.find(_.startsWith("Research Interests Primary")).getOrElse("").stripPrefix("Research Interests Primary").trim
    val interests = blob.find(_.startsWith("Current Research Interests: ")).getOrElse("").stripPrefix("Current Research Interests: ").replaceAllLiterally("\"","").trim
    val web = blob.find(_.startsWith("Web Pages: ")).getOrElse("").stripPrefix("Web Pages: ").trim
    val referees = {
      val i = blob.indexWhere(l => l.startsWith("References"))
      val j = blob.indexWhere(l => l.startsWith("Processed?"))
      for(l <- blob.drop(i + 1).take(j - i)) yield {
        val k = l.indexOf("file (")
        l.take(k).drop(4).replaceAllLiterally(",", "").trim
      }
    }
    println(blob.mkString("\n"))
    Seq(name, currentInstitution, advisor, category, interests, web) ++ referees 
  }

  
  val pw = new PrintWriter(new FileOutputStream(new File("./candidates.csv")))
  for(x <- data) {
    for(p <- x) {
      pw.print("\"" + p + "\",")
    }
    pw.println
  }
  pw.close
}