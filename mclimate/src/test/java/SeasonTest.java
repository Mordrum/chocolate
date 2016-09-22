import com.mordrum.mclimate.common.Season;
import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class SeasonTest {
    @Test
    public void testDates() {
        // September 12th 2016
        assertEquals(Season.getSeasonForCalendar(new GregorianCalendar(2016, 8, 12)), Season.SPRING);
        // September 13th 2016
        assertEquals(Season.getSeasonForCalendar(new GregorianCalendar(2016, 8, 13)), Season.SUMMER);
        // September 14th 2016
        assertEquals(Season.getSeasonForCalendar(new GregorianCalendar(2016, 8, 14)), Season.AUTUMN);
        // September 15th 2016
        assertEquals(Season.getSeasonForCalendar(new GregorianCalendar(2016, 8, 15)), Season.WINTER);
    }
}
