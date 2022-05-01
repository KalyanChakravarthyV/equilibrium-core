package in.vadlakonda.equilibrium.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface EquilibriumAPI {

    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException;

}
