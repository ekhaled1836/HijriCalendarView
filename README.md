# HijriCalendarView
A CalendarView that supports Hijri dates according to Umm-Al-Qura calendar.

## Usage
Usage is simple. In your layout file declare it like you'd declare any other View.

To listen for changes in the selected day, you can provide an interface like so"
```kotlin
    hijriCalendarView.setOnDayPickedEventListener(object : CalendarView.DatePickedEventListener {
        override fun onDatePicked(year: Int, month: Int, day: Int) {
            val ummalquraCalendar = UmmalquraCalendar(year, month, day)
            val gregorianCalendar = GregorianCalendar()
            gregorianCalendar.time = ummalquraCalendar.time
            updateUI(gregorianCalendar)
        }
    })
```

To page to the next/previous month you can call the ```nextMonth()```/```previuosMonth()``` function.

To listen to the new paged month name you can provide a function to the View, like so:

```kotlin
    prayers_calendar.setOnMonthChangedEventListener { newMonthName ->
        changeText(newMonthName)
    }
```

## Screenshots

![Day Theme](https://i.imgur.com/4tVfb7i.png)

![Night Theme](https://i.imgur.com/NuOukZA.png)

