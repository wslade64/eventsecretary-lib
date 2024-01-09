package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Timestamp;
import au.com.eventsecretary.common.TimestampImpl;
import org.hamcrest.MatcherAssert;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import static au.com.eventsecretary.simm.DateUtility.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class DateUtilityTest {

    @Test
    public void compare() {
        assertThat(DateUtility.compare(null, null), is(0));
        assertThat(DateUtility.compare(timestamp(2018, 2, 20, 10, 20, 30), null), is(1));
        assertThat(DateUtility.compare(null, timestamp(2018, 2, 20, 10, 20, 30)), is(-1));
        assertThat(DateUtility.compare(timestamp(2018, 2, 20, 10, 20, 30), timestamp(2018, 2, 20, 10, 20, 30)), is(0));
        assertThat(DateUtility.compare(timestamp(2018, 2, 20, 10, 20, 31), timestamp(2018, 2, 20, 10, 20, 30)), is(1));
        assertThat(DateUtility.compare(timestamp(2018, 2, 20, 10, 20, 29), timestamp(2018, 2, 20, 10, 20, 30)), is(-1));
        assertThat(DateUtility.compare(timestamp(2018, 2, 21, 10, 20, 30), timestamp(2018, 2, 20, 10, 20, 30)), is(1));
        assertThat(DateUtility.compare(timestamp(2018, 2, 19, 10, 20, 30), timestamp(2018, 2, 20, 10, 20, 30)), is(-1));
    }

    static Timestamp timestamp(int year, int month, int day, int hour, int minute, int second) {
        Timestamp timestamp = new TimestampImpl();

        timestamp.setDate(splitToDate(year, month, day));
        timestamp.setTime(splitToTime(hour, minute, second));

        return timestamp;
    }

    @Test
    public void dayOfWeek() {
       assertThat(DateUtility.dayOfWeek(splitToDate(2019, 1, 24)), is("Thursday"));
        assertThat(DateUtility.dayOfWeek(splitToDate(2020, 12, 31)), is("Thursday"));
    }

    @Test
    public void addDay() {
        assertThat(DateUtility.addDay(splitToDate(2019, 1, 24), 1), is(20190125));
        assertThat(DateUtility.addDay(splitToDate(2019, 1, 31), 1), is(20190201));
    }

    @Test
    public void shouldConvertTimes() {
        LocalTime localTime = new LocalTime(23, 59, 59);
        int time = DateUtility.time(localTime);
        LocalTime converted = DateUtility.time(time);
        MatcherAssert.assertThat(converted, is(localTime));

        LocalTime time1 = DateUtility.time(400);
        MatcherAssert.assertThat(time1.getHourOfDay(), is(0));
        MatcherAssert.assertThat(time1.getMinuteOfHour(), is(4));
        MatcherAssert.assertThat(time1.getSecondOfMinute(), is(0));


        MatcherAssert.assertThat(DateUtility.timeString(120100), is("12.01pm"));
        MatcherAssert.assertThat(DateUtility.timeString(130000), is("1.00pm"));
        MatcherAssert.assertThat(DateUtility.timeString(10000), is("1.00am"));
        MatcherAssert.assertThat(DateUtility.timeString(0), is("12.00am"));
    }

    @Test
    public void shouldConvertDays() {
        LocalDate localDate = new LocalDate(2016, 2, 29);
        int date = DateUtility.date(localDate);
        MatcherAssert.assertThat(date, is(20160229));

        LocalDate localDate2 = DateUtility.date(20160229);
        MatcherAssert.assertThat(DateUtility.dateString(localDate2), is("2016-02-29"));

        MatcherAssert.assertThat(DateUtility.dateString(localDate), is("2016-02-29"));
        LocalDate converted = DateUtility.date(date);
        MatcherAssert.assertThat(converted, is(localDate));

        MatcherAssert.assertThat(DateUtility.dateString(DateUtility.date(DateUtility.date("2016-02-29"))), is("2016-02-29"));
    }

    @Test
    public void shouldConvertLastDay() {
        LocalDate localDate = new LocalDate(2016, 12, 31);
        int date = DateUtility.date(localDate);
        MatcherAssert.assertThat(date, is(20161231));

        LocalDate localDate2 = DateUtility.date(20161231);
        MatcherAssert.assertThat(DateUtility.dateString(localDate2), is("2016-12-31"));

        MatcherAssert.assertThat(DateUtility.dateString(localDate), is("2016-12-31"));
        LocalDate converted = DateUtility.date(date);
        MatcherAssert.assertThat(converted, is(localDate));

        MatcherAssert.assertThat(DateUtility.dateString(DateUtility.date(DateUtility.date("2016-12-31"))), is("2016-12-31"));
    }

    @Test
    public void shouldCalculateDaysBetween() {
        MatcherAssert.assertThat(DateUtility.daysBetween(20180101, 20180101), is(0));
        MatcherAssert.assertThat(DateUtility.daysBetween(20180101, 20180102), is(1));
        MatcherAssert.assertThat(DateUtility.daysBetween(20180102, 20180101), is(-1));
        MatcherAssert.assertThat(DateUtility.daysBetween(20171231, 20180101), is(1));
    }

    @Test
    public void shouldAddDays() {
        MatcherAssert.assertThat(DateUtility.daysAdd(20180101, 1), is(20180102));
        MatcherAssert.assertThat(DateUtility.daysAdd(20171231, 2), is(20180102));
        MatcherAssert.assertThat(DateUtility.daysAdd(20180228, 1), is(20180301));
        MatcherAssert.assertThat(DateUtility.daysAdd(20160228, 1), is(20160229));
    }

    @Test
    public void shouldParseTimestamp() {
        Timestamp timestamp = DateUtility.toTimestamp("2019-01-01 09:30:43");
        MatcherAssert.assertThat(timestamp.getDate(), is(20190101));
        MatcherAssert.assertThat(timestamp.getTime(), is(93043));
    }

    @Test
    public void shouldFormatMilliSeconds() {
        assertThat(DateUtility.formatMilliSeconds(8000), is("0:08.00"));
        assertThat(DateUtility.formatMilliSeconds(88000), is("1:28.00"));
    }

    @Test
    public void shouldMachineFormat() {
        assertThat(DateUtility.machineTimestamp(null), is(""));
        assertThat(DateUtility.machineTimestamp(DateUtility.createTimestamp(20240130, 159)), is("20240130-000159"));
    }
}