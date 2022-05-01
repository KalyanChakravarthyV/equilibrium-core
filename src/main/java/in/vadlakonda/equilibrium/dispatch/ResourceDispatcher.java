package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.api.EquilibriumAPIFactory;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceDispatcher implements RequestDispatcher{

    private Dispatcher dispatcher;

    private EquilibriumAPIFactory equilibriumAPIFactory;
    public ResourceDispatcher(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response,ClassLoader classLoader) throws ServletException {
        
        

    }
}
