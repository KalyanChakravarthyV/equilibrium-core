package in.vadlakonda.equilibrium.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface EquilibriumAPI {

    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException;

}
