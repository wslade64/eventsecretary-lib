package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Period;
import au.com.eventsecretary.common.PeriodImpl;
import au.com.eventsecretary.common.Timestamp;
import au.com.eventsecretary.common.TimestampImpl;
import au.com.eventsecretary.facility.site.Site;
import au.com.eventsecretary.people.Address;
import au.com.eventsecretary.people.States;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

    static Date dateForTimestamp(Timestamp timestamp) {
        int[] date = dateToSplit(timestamp.getDate());
        int[] time = timeToSplit(timestamp.getTime());
        return new LocalDateTime(date[0], date[1], date[2], time[0], time[1], time[2]).toDate();
    }

    static Date dateForDate(int date) {
        int[] dates = dateToSplit(date);
        return new LocalDate(dates[0], dates[1], dates[2]).toDate();
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

    static Period createPeriodForDayOfYear(int dayOfYear) {
        int now = DateUtility.nowDate();
        int[] nowSplit = DateUtility.dateToSplit(now);
        int[] start = DateUtility.dateToSplit(dayOfYear);
        start[0] = nowSplit[0];
        int startDate = DateUtility.splitToDate(start[0], start[1], start[2]);
        if (startDate > now) {
            startDate = DateUtility.splitToDate(start[0] - 1, start[1], start[2]);
        }
        int[] end = DateUtility.dateToSplit(dayOfYear);
        end[0] = nowSplit[0] + 1;
        return DateUtility.createPeriod(startDate, DateUtility.splitToDate(end[0], end[1], end[2]));
    }

    static final int MINUTES_DAY = 24 * 60;

    static void addMinutes(Timestamp current, int minutes) {

        int time = addMinutes(current.getTime(), minutes);
        minutes = minutes(time);
        if (minutes < MINUTES_DAY) {
            current.setTime(time);
        } else {
            int minutesInDay = minutes % MINUTES_DAY;
            int days = minutes / MINUTES_DAY;
            current.setTime(timeMinutes(minutesInDay));
            current.setDate(addDay(current.getDate(), days));
        }

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

    static String longerTime(Integer time) {
        if (time == null || time == 0) {
            return "";
        }
        int[] split = timeToSplit(time);
        return String.format("%02d:%02d:%02d", split[0], split[1], split[2]);
    }

    static int minutes(int time) {
        int[] intervals = DateUtility.timeToSplit(time);
        return intervals[0] * 60 + intervals[1];
    }

    static int timeMinutes(int minutes) {
        int min = minutes % 60;
        int hours = minutes / 60;
        return splitToTime(hours, min, 0);
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

    static Timestamp now(String timezoneId) {
        if (timezoneId == null) {
            return now();
        }
        DateTimeZone dateTimeZone = DateTimeZone.forID(timezoneId);
        LocalDate localDate = LocalDate.now(dateTimeZone);
        LocalTime localTime = LocalTime.now(dateTimeZone);

        Timestamp timestamp = new TimestampImpl();
        timestamp.setDate(splitToDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));
        timestamp.setTime(splitToTime(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute()));
        return timestamp;
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

    static int addMonth(int date, int months) {
        LocalDate localDate = dateToLocal(date);
        localDate = localDate.plusMonths(months);
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

    static String formatMilliSeconds(BigDecimal milliseconds) {
        if (milliseconds == null) {
            return "";
        }
        return formatMilliSeconds(milliseconds.intValue());
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

    static void periodRange(Period sourcePeriod, Period mergePeriod) {
        if (mergePeriod == null) {
            return;
        }
        if (sourcePeriod.getStart() == null || DateUtility.compare(mergePeriod.getStart(), sourcePeriod.getStart()) < 0) {
            sourcePeriod.setStart(mergePeriod.getStart());
        }
        if (sourcePeriod.getEnd() == null || DateUtility.compare(mergePeriod.getEnd(), sourcePeriod.getEnd()) > 0) {
            sourcePeriod.setEnd(mergePeriod.getEnd());
        }
    }

    static boolean withinPeriod(Period period, int applicableDate) {
        if (period == null) {
            return true;
        }
        if (applicableDate < period.getStart().getDate()) {
            return false;
        }
        if (period.getEnd() != null && applicableDate > period.getEnd().getDate()) {
            return false;
        }
        return true;
    }

    static Map<States, String> stateTimezoneMap = initMap();

    static Map<States, String> initMap() {
        Map<States, String> map = new HashMap<>();
        map.put(States.VIC, "Australia/Victoria");
        map.put(States.NSW, "Australia/NSW");
        map.put(States.SA, "Australia/South");
        map.put(States.WA, "Australia/West");
        map.put(States.TAS, "Australia/Tasmania");
        map.put(States.QLD, "Australia/Queensland");
        map.put(States.ACT, "Australia/ACT");
        map.put(States.NT, "Australia/North");
        return map;
    }

    static String timezoneId(Site site) {
        if (site == null) {
            return null;
        }
        Address streetAddress = site.getStreetAddress();
        if (streetAddress == null) {
            return null;
        }
        States state = streetAddress.getState();
        return stateTimezoneMap.get(state);
    }

    /**
     * @return Time in seconds from GMT
     */
    static int timezoneOffset(Site site) {
        String tz = timezoneId(site);
        TimeZone timeZone = TimeZone.getTimeZone(tz);
        return timeZone.getOffset(System.currentTimeMillis()) / (60 * 1000);
    }

}
