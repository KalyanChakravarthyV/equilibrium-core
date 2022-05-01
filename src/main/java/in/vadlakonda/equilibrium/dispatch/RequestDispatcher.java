package in.vadlakonda.equilibrium.dispatch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RequestDispatcher {


    public void dispatch(HttpServletRequest request, HttpServletResponse response,ClassLoader classLoader) throws ServletException, IOException;

}
