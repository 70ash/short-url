import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Author 70ash
 * Date 2024/5/14 下午11:07
 * Type:
 * Description:
 */
public class StreamDemo {
    @Test
    public void test() {
        int weekOfMonth = 5; // 当前日期是这个月的第3周

        LocalDate currentDate = getDateByWeekOfMonthAndDayOfWeek(weekOfMonth, 4);

        System.out.println("当前日期是: " + currentDate);
    }

    public static LocalDate getDateByWeekOfMonthAndDayOfWeek(int weekOfMonth, int dayOfWeek) {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // 找到这个月第一周的星期X
        LocalDate firstWeekday = firstDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)));

        // 计算当前日期应该在这个月的第几周
        int daysToAdd = (weekOfMonth - 1) * 7;
        return firstWeekday.plusDays(daysToAdd);
    }
}
