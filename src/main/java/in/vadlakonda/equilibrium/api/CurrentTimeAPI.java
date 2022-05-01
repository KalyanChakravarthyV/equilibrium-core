package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CurrentTimeAPI implements EquilibriumAPI {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException {

        Locale locales[] = SimpleDateFormat.getAvailableLocales();

        HashMap<String, HashMap<String, String>> dateTimeMap = new HashMap<String, HashMap<String, String>>();

        Date currentDate = new Date();

        for (Locale l : locales) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, l);
            String formattedDate = df.format(currentDate);

            HashMap<String, String> localeBasedDateTimeMap = new HashMap<String, String>();
            localeBasedDateTimeMap.put("name", l.getDisplayName());
            localeBasedDateTimeMap.put("locale_name", l.getDisplayName(l));
            localeBasedDateTimeMap.put("time", formattedDate);


            dateTimeMap.put(l.toString(), localeBasedDateTimeMap);
        }

        try {
            response.getWriter().print(new Gson().toJson(dateTimeMap));
        } catch (IOException e) {
            throw new APIException(500, e.getMessage());
        }
    }
}
