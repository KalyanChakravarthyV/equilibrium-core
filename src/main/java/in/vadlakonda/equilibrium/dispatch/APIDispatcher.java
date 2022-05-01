package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.api.APIException;
import in.vadlakonda.equilibrium.api.EquilibriumAPIFactory;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class APIDispatcher implements RequestDispatcher{

    private Dispatcher dispatcher;

    private EquilibriumAPIFactory equilibriumAPIFactory;
    public APIDispatcher(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
        equilibriumAPIFactory = EquilibriumAPIFactory.getEquilibriumAPIFactory(dispatcher.getConfigFile());
    }
    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response,ClassLoader classLoader) throws ServletException, IOException {

        try {
            equilibriumAPIFactory.getEquilibriumAPI(request).execute(request,response,classLoader);
        } catch (APIException e) {
            response.setStatus(e.getStatusCode());
            response.getWriter().print(e.getMessage());
        }


    }
}
