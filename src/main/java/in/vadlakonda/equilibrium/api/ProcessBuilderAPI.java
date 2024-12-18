package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.api.request.Payload;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProcessBuilderAPI extends AbstractAPI {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ProcessBuilderAPI.class);


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException {

        this.initializeAndValidate(request, response);

        //payload is read from the request
        executeProcess(this.payload, response);

    }

    private void executeProcess(Payload payload, HttpServletResponse response) {
    }
}
