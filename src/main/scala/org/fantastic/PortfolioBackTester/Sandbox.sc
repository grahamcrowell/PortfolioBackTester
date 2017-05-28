import java.text.SimpleDateFormat
import java.util.Calendar

val today = Calendar.MONDAY

val saturday = Calendar.getInstance()
val sunday = Calendar.getInstance()

saturday.set(2017, 4, 27)
saturday.get(Calendar.DAY_OF_WEEK)

sunday.set(2017, 4, 28)
sunday.get(Calendar.DAY_OF_WEEK)

val s = new SimpleDateFormat("yyyyMMdd")
s.format(saturday.getTime()).toInt

val monday = Calendar.getInstance() //sets saturday as Calendar type
monday.set(2017, 4, 29,  12, 3 )
monday.get(Calendar.DAY_OF_WEEK)
monday.getTime



