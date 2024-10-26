package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CurrentTimeAPI extends AbstractAPI {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException {

        Locale locales[] = SimpleDateFormat.getAvailableLocales();

        HashMap<String, HashMap<String, String>> dateTimeMap = new HashMap<String, HashMap<String, String>>();

        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        Date currentDate = new Date();

        for (Locale l : locales) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, l);

            HashMap<String, String> localeBasedDateTimeMap = new HashMap<String, String>();
            localeBasedDateTimeMap.put("name", l.getDisplayName());
            localeBasedDateTimeMap.put("locale_name", l.getDisplayName(l));
            localeBasedDateTimeMap.put("date", df.format(currentDate));
            localeBasedDateTimeMap.put("time", ZonedDateTime.now().format(dtf.withLocale(l)));



            dateTimeMap.put(l.toString(), localeBasedDateTimeMap);
        }

        try {
            response.setContentType(CONTENT_TYPE_JSON);
            response.getWriter().print( GSON_BUILDER.create().toJson(dateTimeMap));
        } catch (IOException e) {
            throw new APIException(500, e.getMessage());
        }
    }
}
