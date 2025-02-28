import javafx.util.StringConverter;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeStringConverter extends StringConverter<Time> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public String toString(Time time) {
        return (time == null) ? "" : time.toLocalTime().format(formatter);
    }

    @Override
    public Time fromString(String string) {
        try {
            return Time.valueOf(LocalTime.parse(string, formatter));
        } catch (Exception e) {
            System.err.println("❌ Invalid time format: " + string);
            return null;
        }
    }
}
