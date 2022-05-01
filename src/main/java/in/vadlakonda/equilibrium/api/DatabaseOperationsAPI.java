package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.vadlakonda.equilibrium.api.config.APIConfig;
import in.vadlakonda.equilibrium.api.request.Payload;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseOperationsAPI implements EquilibriumAPI {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPI.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException {


        BufferedReader bufferedReader = null;

        try {
            BufferedReader reader  = request.getReader();
            if (reader != null) {
                Gson gson = new Gson();

                Payload payload = gson.fromJson(reader, Payload.class);


                response.getWriter().print(gson.toJson(payload));


            } else {
                log.error("No input stream found!");
            }
        } catch (IOException ex) {
            throw new APIException(500, ex.getMessage());
        }
    }
}
