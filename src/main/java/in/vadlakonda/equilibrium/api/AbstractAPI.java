package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.vadlakonda.equilibrium.api.request.Payload;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;


public abstract class AbstractAPI implements EquilibriumAPI {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ProcessBuilderAPI.class);

    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final GsonBuilder GSON_BUILDER = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    protected Payload payload = null;

    public void initializeAndValidate(HttpServletRequest request, HttpServletResponse response) throws APIException, IOException {
        BufferedReader bufferedReader = null;
            BufferedReader reader = request.getReader();
            if (reader != null) {
                Gson gson = new Gson();
                payload = gson.fromJson(reader, Payload.class);
            } else {
                //reader is null
                throw new APIException(HTTP_BAD_REQUEST, "Request body cannot be empty");
            }

            if (payload == null) {
                throw new APIException(HTTP_BAD_REQUEST, "Request body(payload) cannot be empty");
            }


    }
}
