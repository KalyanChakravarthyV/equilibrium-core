package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.api.APIException;
import in.vadlakonda.equilibrium.api.EquilibriumAPI;
import in.vadlakonda.equilibrium.api.EquilibriumAPIFactory;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class APIDispatcher implements RequestDispatcher {

    private Dispatcher dispatcher;

    private EquilibriumAPIFactory equilibriumAPIFactory;

    public APIDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws ServletException, IOException {

        try {
            if (equilibriumAPIFactory == null)
                equilibriumAPIFactory = EquilibriumAPIFactory.getEquilibriumAPIFactory(dispatcher.getConfigFile(), classLoader);

            EquilibriumAPI api = equilibriumAPIFactory.getEquilibriumAPI(request);

            if(api == null) {

                String baseURI = equilibriumAPIFactory.getAPIConfig().getBaseURI();

                String operationURI = StringUtils.substringAfter(request.getRequestURI(), baseURI);

                throw new APIException(HTTP_BAD_REQUEST, "Unsupported API:" +operationURI);

            }else
                api.execute(request, response, classLoader);

        } catch (APIException e) {
            response.setStatus(e.getStatusCode());
            response.getWriter().print(e.getMessage());
        }


    }
}
