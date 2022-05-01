package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.api.EquilibriumAPIFactory;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponseDispatcher implements RequestDispatcher{


    private Dispatcher dispatcher;

    public ErrorResponseDispatcher(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }
    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response,ClassLoader classLoader) throws ServletException, IOException {
        
        response.getWriter().println("{\"error\":\"Error Occurred\"}");

    }
}
