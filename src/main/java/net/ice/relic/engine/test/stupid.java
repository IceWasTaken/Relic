package net.ice.relic.engine.test;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class stupid {

    private static final int INTERVAL_MINUTES = 20;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

    public static void main(String[] args) {
        List<String> entries = new ArrayList<>();

        LocalDate sampleDate = LocalDate.of(2025, 7, 16);
        LocalTime sampleStart = LocalTime.of(14, 0);
        LocalTime sampleEnd = sampleStart.plusMinutes(INTERVAL_MINUTES);
        String sampleEntry = formatEntry(sampleDate, 20, sampleStart, sampleEnd);
        System.out.println(sampleEntry);

        LocalDate startDate = LocalDate.of(2024, 8, 20);
        LocalDate endDate = LocalDate.of(2025, 6, 13);

        LocalTime morningStart = LocalTime.of(11, 0);
        LocalTime afternoonStart = LocalTime.of(14, 0);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                entries.add(formatEntry(date, 20, morningStart, morningStart.plusMinutes(INTERVAL_MINUTES)));
                entries.add(formatEntry(date, 20, afternoonStart, afternoonStart.plusMinutes(INTERVAL_MINUTES)));
            }
        }

        for (String entry : entries) {
            System.out.println(entry);
        }

        int totalEntries = entries.size();
        int totalMinutes = totalEntries * INTERVAL_MINUTES;
        double totalHours = totalMinutes / 60.0;

        System.out.println("\nTotal entries: " + totalEntries);
        System.out.println("Total time: " + totalMinutes + " minutes (" + totalHours + " hours)");
    }

    private static String formatEntry(LocalDate date, int value, LocalTime start, LocalTime end) {
        return String.format("%s - %d - %s - %s",
                date.format(DATE_FORMAT),
                value,
                start.format(TIME_FORMAT),
                end.format(TIME_FORMAT));
    }
}
