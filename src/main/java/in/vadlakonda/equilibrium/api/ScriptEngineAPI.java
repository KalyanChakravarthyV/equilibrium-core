package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.api.request.Payload;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ScriptEngineAPI extends AbstractAPI {
    private static final Logger log = Logger.getLogger(ScriptEngineAPI.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException {

        this.initializeAndValidate(request, response);

        //payload is read from the request
        executeScript(this.payload, response);

    }

    private void executeScript(Payload payload, HttpServletResponse response) {


    }
}
