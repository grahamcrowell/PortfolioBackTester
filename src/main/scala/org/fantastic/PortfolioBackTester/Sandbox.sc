import java.text.SimpleDateFormat
import java.util.Calendar

val today = Calendar.getInstance()
val dtFmt = new SimpleDateFormat("yyyyMMdd")
today.set(Calendar.YEAR, 2017)
today.set(Calendar.MONTH, Calendar.MAY)
today.set(Calendar.DAY_OF_MONTH, 1)
dtFmt.format(today.getTime)
today.toString

