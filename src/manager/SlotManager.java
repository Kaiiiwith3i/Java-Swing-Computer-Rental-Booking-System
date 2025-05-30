package manager;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class SlotManager {
    public static final int NUM_COMPUTERS = 20;
    public static final int NUM_TIME_SLOTS = 10;

    private final Map<LocalDate, boolean[][]> bookedMap = new HashMap<>();
    private final Map<LocalDate, String[][]> bookedByMap = new HashMap<>();

    private void ensureDate(LocalDate date) {
        bookedMap.computeIfAbsent(date, d -> new boolean[NUM_COMPUTERS][NUM_TIME_SLOTS]);
        bookedByMap.computeIfAbsent(date, d -> new String[NUM_COMPUTERS][NUM_TIME_SLOTS]);
    }

    public boolean bookSlot(LocalDate date, int computerIndex, int timeIndex, String username) {
        ensureDate(date);
        if (bookedMap.get(date)[computerIndex][timeIndex]) return false;
        bookedMap.get(date)[computerIndex][timeIndex] = true;
        bookedByMap.get(date)[computerIndex][timeIndex] = username;
        return true;
    }

    public void clearSlot(LocalDate date, int computerIndex, int timeIndex) {
        ensureDate(date);
        bookedMap.get(date)[computerIndex][timeIndex] = false;
        bookedByMap.get(date)[computerIndex][timeIndex] = null;
    }

    public boolean isSlotBooked(LocalDate date, int computerIndex, int timeIndex) {
        ensureDate(date);
        return bookedMap.get(date)[computerIndex][timeIndex];
    }

    public String getBookedBy(LocalDate date, int computerIndex, int timeIndex) {
        ensureDate(date);
        return bookedByMap.get(date)[computerIndex][timeIndex];
    }

    public void releaseExpiredBookings(LocalDate date, DefaultTableModel model) {
        ensureDate(date);
        LocalTime now = LocalTime.now();
        for (int i = 0; i < NUM_COMPUTERS; i++) {
            for (int j = 0; j < NUM_TIME_SLOTS; j++) {
                int bookingHour = j + 13;
                if (bookedMap.get(date)[i][j] && date.equals(LocalDate.now()) && now.getHour() > bookingHour) {
                    bookedMap.get(date)[i][j] = false;
                    bookedByMap.get(date)[i][j] = null;
                    model.setValueAt("Available", i, j + 1);
                }
            }
        }
    }

    public void populateTable(LocalDate date, DefaultTableModel model) {
        ensureDate(date);
        for (int i = 0; i < NUM_COMPUTERS; i++) {
            for (int j = 0; j < NUM_TIME_SLOTS; j++) {
                if (bookedMap.get(date)[i][j]) {
                    model.setValueAt("Booked by " + bookedByMap.get(date)[i][j], i, j + 1);
                } else {
                    model.setValueAt("Available", i, j + 1);
                }
            }
        }
    }
}
