package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Period;
import au.com.eventsecretary.common.PeriodImpl;
import au.com.eventsecretary.common.Timestamp;
import au.com.eventsecretary.common.TimestampImpl;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface DateUtility {
    int MONTH = 100;
    int YEAR = MONTH * 100;

    int MINUTE = 100;
    int HOUR = MINUTE * 100;

    static String smartTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        if (timestamp.getTime() == 0) {
            return DateUtility.longDate(timestamp.getDate());
        }
        return DateUtility.longDate(timestamp.getDate()) + " " + DateUtility.shortTime(timestamp.getTime());
    }

    static int time(LocalTime localDate) {
        int hour = localDate.getHourOfDay();
        int minute = localDate.getMinuteOfHour();
        int second = localDate.getSecondOfMinute();
        return hour * HOUR + minute * MINUTE + second;
    }

    static LocalTime time(int number) {
        int hour = number / HOUR;
        int minute = number % HOUR / MINUTE;
        int seconds = number % MINUTE;

        return new LocalTime(hour, minute, seconds);
    }

    static int date(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonthOfYear();
        int day = localDate.getDayOfMonth();
        return year * YEAR + month * MONTH + day;
    }

    static LocalDate dateLocal(String date) {
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(date);
    }

    static int date(String date) {
        return date(dateLocal(date));
    }

    static LocalDate date(int number) {
        int year = number / YEAR;
        int month = number % YEAR / MONTH;
        int day = number % MONTH;

        return new LocalDate(year, month, day);
    }

    static String dateString(int number) {
        LocalDate date = date(number);
        return dateString(date);
    }

    static String dateString(LocalDate date) {
        return String.format("%d-%02d-%02d", date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    static String timeString(int number) {
        LocalTime time = time(number);
        return timeString(time);
    }

    static String timeString(LocalTime time) {
        int hourOfDay = time.getHourOfDay();
        String meridian = hourOfDay >= 12 ? "pm" : "am";
        if (hourOfDay > 12) {
            hourOfDay -= 12;
        } else if (hourOfDay == 0) {
            hourOfDay = 12;
        }
        return String.format("%d.%02d%s", hourOfDay, time.getMinuteOfHour(), meridian);
    }

    static int daysBetween(int from, int to) {
        return Days.daysBetween(date(from), date(to)).getDays();
    }

    static int daysAdd(int from, int days) {
        return date(date(from).plusDays(days));
    }

    static boolean between(int point, int from, int to) {
        return point >= from && point < to;
    }

    static boolean overlap(int aFrom, int aTo, int bFrom, int bTo) {
        return !(aFrom >= bTo || aTo <= bFrom);
    }

    static boolean betweenInclusive(int point, int from, int to) {
        return point >= from && point <= to;
    }

    static int compare(Timestamp timestamp1, Timestamp timestamp2) {
        if (timestamp1 == null && timestamp2 == null) {
            return 0;
        }
        if (timestamp1 == null) {
            return -1;
        }
        if (timestamp2 == null) {
            return 1;
        }
        int result = timestamp1.getDate() - timestamp2.getDate();
        if (result != 0) {
            return result;
        }
        return timestamp1.getTime() - timestamp2.getTime();
    }

    static boolean after(Timestamp timestamp1, Timestamp timestamp2) {
        return compare(timestamp1, timestamp2) > 0;
    }
    static boolean before(Timestamp timestamp1, Timestamp timestamp2) {
        return compare(timestamp1, timestamp2) < 0;
    }

    static int splitToDate(int year, int month, int day) {
        return year * YEAR + month * MONTH + day;
    }

    static int[] dateToSplit(int date) {
        return new int[] {
                date / YEAR,
                date % YEAR / MONTH,
                date % MONTH
        };
    }

    static Timestamp clone(Timestamp timestamp) {
        TimestampImpl clone = new TimestampImpl();
        clone.setDate(timestamp.getDate());
        clone.setTime(timestamp.getTime());
        return clone;
    }

    static Timestamp createTimestamp(Date date) {
        return createTimestamp(date(LocalDate.fromDateFields(date)), time(LocalTime.fromDateFields(date)));
    }

    static Timestamp createTimestamp(int date, int time) {
        TimestampImpl clone = new TimestampImpl();
        clone.setDate(date);
        clone.setTime(time);
        return clone;
    }

    static Period createPeriod(int dateFrom, int dateTo) {
        Period period = new PeriodImpl();
        period.setStart(createTimestamp(dateFrom, 0));
        period.setEnd(createTimestamp(dateTo, 0));
        return period;
    }

    static Period createPeriod(Timestamp from, Timestamp to) {
        Period period = new PeriodImpl();
        period.setStart(from);
        period.setEnd(to);
        return period;
    }

    static void addMinutes(Timestamp current, int minutes) {
        current.setTime(addMinutes(current.getTime(), minutes));
    }

    static int splitToTime(int hour, int minute, int second) {
        return hour * HOUR + minute * MINUTE + second;
    }

    static int[] timeToSplit(int time) {
        return new int[] {
                time / HOUR,
                time % HOUR / MINUTE,
                time % MINUTE
        };
    }

    static String shortTime(int time) {
        if (time == 0) {
            return "";
        }
        int hour = time / HOUR;
        int minute = time % HOUR / MINUTE;
        int seconds = time % MINUTE;

        String meridian = hour >= 12 ? "pm" : "am";
        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        String sminute = (minute > 9) ? Integer.toString(minute) : ("0" + minute);
        return hour + ":" + sminute + meridian;
    }

    static String shorterTime(int time) {
        if (time == 0) {
            return "";
        }
        int hour = time / HOUR;
        int minute = time % HOUR / MINUTE;

        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        String sminute = (minute > 9) ? Integer.toString(minute) : ("0" + minute);
        return hour + ":" + sminute;
    }

    static int minutes(int time) {
        int[] intervals = DateUtility.timeToSplit(time);
        return intervals[0] * 60 + intervals[1];
    }

    static int addMinutes(int time, int minutes) {
        int extraHours = minutes / 60;
        int extraMinutes = minutes % 60;

        int[] intervals = DateUtility.timeToSplit(time);
        intervals[0] += extraHours;
        intervals[1] += extraMinutes;
        if (intervals[1] >= 60) {
            intervals[1] -= 60;
            intervals[0]++;
        }

        return DateUtility.splitToTime(intervals[0], intervals[1], intervals[2]);
    }

    static Timestamp now() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();

        Timestamp timestamp = new TimestampImpl();
        timestamp.setDate(splitToDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
        timestamp.setTime(splitToTime(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute()));
        return timestamp;
    }

    static int nowDate() {
        LocalDate localDate = LocalDate.now();
        return splitToDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    static String shorterTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return shorterTime(timestamp.getTime());
    }

    static int addDay(int date, int days) {
        LocalDate localDate = dateToLocal(date);
        localDate = localDate.plusDays(days);
        return splitToDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    static int addYear(int date, int years) {
        int[] ints = dateToSplit(date);
        ints[0] += years;
        return splitToDate(ints[0], ints[1], ints[2]);
    }

    static String dayOfWeek(int date) {
        return dateToLocal(date).dayOfWeek().getAsText();
    }

    static LocalDate dateToLocal(int date) {
        int[] ints = dateToSplit(date);
        return new LocalDate(ints[0], ints[1], ints[2]);
    }

    static LocalTime timeToLocal(int time) {
        int[] ints = timeToSplit(time);
        return new LocalTime(ints[0], ints[1], ints[2]);
    }

    static boolean isInRange(int eventDate, int startDate, Integer endDate) {
        return eventDate >= startDate && (endDate == null || eventDate < endDate);
    }

    static int subtract(int time1, int time) {
        Seconds seconds = Seconds.secondsBetween(timeToLocal(time), timeToLocal(time1));
        return seconds.getSeconds();
    }

    static Timestamp toTimestamp(String intervalName) {
        LocalDateTime localDateTime = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss").parseLocalDateTime(intervalName);
        Timestamp timestamp = new TimestampImpl();
        timestamp.setDate(splitToDate(localDateTime.getYear(), localDateTime.getMonthOfYear(), localDateTime.getDayOfMonth()));
        timestamp.setTime(splitToTime(localDateTime.getHourOfDay(), localDateTime.getMinuteOfHour(), localDateTime.getSecondOfMinute()));
        return timestamp;
    }

    String[] shortMonths = {
      "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    String[] longMonths = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    static String shortDuration(int date, Integer duration) {
        if (duration == null || duration <= 1) {
            return shortDay(date);
        }

        int[] startParts = DateUtility.dateToSplit(date);
        int endDate = DateUtility.addDay(date, duration - 1);
        int[] endParts = DateUtility.dateToSplit(endDate);

        if (startParts[1] == endParts[1]) {
            return String.format("%d-%d %s %s", startParts[2], endParts[2], shortMonths[startParts[1] - 1], startParts[0]);
        } else if (startParts[0] == endParts[0]) {
            return String.format("%d %s to %d %s %s", startParts[2], shortMonths[startParts[1] - 1], endParts[2], shortMonths[endParts[1] - 1], startParts[0]);
        } else {
            return String.format("%s to %s", shortDay(date), shortDay(endDate));
        }
    }

    static String shortDate(int date) {
        if (date == 0) {
            return "";
        }

        int[] ints = dateToSplit(date);

        return shortMonths[ints[1] - 1] + " " + ints[2];
    }

    static String shortDay(int date) {

        if (date == 0) {
            return "";
        }

        int[] ints = dateToSplit(date);

        return ints[2] + " " + shortMonths[ints[1] - 1] + " " + ints[0];
    }

    static String longDate(int date) {

        if (date == 0) {
            return "";
        }

        int[] ints = dateToSplit(date);

        return ints[2] + " " + longMonths[ints[1] - 1] + " " + ints[0];
    }

    static Timestamp timestamp(LocalDateTime localDateTime) {
        Timestamp timestamp = new TimestampImpl();
        timestamp.setDate(date(localDateTime.toLocalDate()));
        timestamp.setTime(time(localDateTime.toLocalTime()));
        return timestamp;
    }

    static Timestamp endOfDay(int date) {
        Timestamp timestamp = new TimestampImpl();
        timestamp.setDate(date);
        timestamp.setTime(235959);
        return timestamp;
    }

    static int age(int dateOfBirth, int reference){
        if (dateOfBirth > reference) {
            return 0;
        }
        int[] dobSplit = dateToSplit(dateOfBirth);
        int[] refSplit = dateToSplit(reference);
        int years = refSplit[0] - dobSplit[0];
        if (refSplit[2] < dobSplit[2]) {
            refSplit[1]--;
        }
        if (refSplit[1] < dobSplit[1]) {
            years--;
        }
        return years;
    }

    static String formatMilliSeconds(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hms = milliseconds % 1000;
        int tms = hms / 10;
        return String.format("%d:%02d.%02d", minutes, seconds, tms);
    }

    static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
