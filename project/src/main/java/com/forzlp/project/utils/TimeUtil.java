package com.forzlp.project.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Author 70ash
 * Date 2024/5/30 下午9:06
 * Description:
 */
public class TimeUtil {
    // 要根据星期几和当前日期是这个月的第几周来获取当前日期
    public static LocalDate getDateByWeekOfMonthAndDayOfWeek(int weekOfMonth, int dayOfWeek) {
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // 找到这个月第一周的星期X
        LocalDate firstWeekday = firstDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)));

        // 计算当前日期应该在这个月的第几周
        int daysToAdd = (weekOfMonth - 1) * 7;
        return firstWeekday.plusDays(daysToAdd);
    }

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        return date.format(formatter);
    }
}
