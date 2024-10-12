package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import in.vadlakonda.equilibrium.api.request.Payload;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class ProcessBuilderAPI implements EquilibriumAPI {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ProcessBuilderAPI.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException {
        BufferedReader bufferedReader = null;
        Payload payload = null;
        try {
            BufferedReader reader = request.getReader();
            if (reader != null) {
                Gson gson = new Gson();
                payload = gson.fromJson(reader, Payload.class);
            } else {
                //reader is null
                throw new APIException(400, "Request body cannot be empty");
            }

            if (payload == null) {
                throw new APIException(400, "Request body(payload) cannot be empty");
            }

            executeProcess(payload, response);


        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new APIException(500, e.getMessage());

        }
    }

    private void executeProcess(Payload payload, HttpServletResponse response) {
    }
}
