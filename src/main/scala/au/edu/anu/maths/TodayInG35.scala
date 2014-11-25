package au.edu.anu.maths

import java.util.Date
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import scala.io.Source
import java.util.Calendar

object TodayInG35 extends App {

  val bookingURL = {
    val cal = Calendar.getInstance()
    cal.setTime(new Date());
    val year = cal.get(Calendar.YEAR);
    val month = cal.get(Calendar.MONTH) + 1;
    val day = cal.get(Calendar.DAY_OF_MONTH);
    s"https://maths-intranet.anu.edu.au/bookings/day.php?year=$year&month=$month&day=$day&area=1"
  }

  val eventRegex = "view_entry.php[^\"]*".r
  val todaysEvents = eventRegex.findAllIn(Source.fromURL(bookingURL).getLines.mkString("\n"))

  for (
    event <- todaysEvents;
    eventURL = "https://maths-intranet.anu.edu.au/bookings/" + event;
    source = Source.fromURL(eventURL).getLines.mkString("");
    if source.contains("John Dedman Bldg - Room G35")
  ) {
    // obligatory: http://stackoverflow.com/a/1732454/82970
    val title = "<h3>(.*)</h3>".r.findFirstMatchIn(source).get.group(1)
    println(title)
    val startTime = "<td>Start Time:</td> *<td>([^<]*)</td>".r.findFirstMatchIn(source).get.group(1)
    println(startTime)
  }

}