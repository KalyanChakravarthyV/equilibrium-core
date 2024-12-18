package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.TestConstants;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static in.vadlakonda.equilibrium.TestConstants.APP_ROOT;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

class CurrentTimeAPITest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(CurrentTimeAPITest.class);

    @Test
    void execute() throws IOException, ServletException {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/currentTimeAPI");

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader, APP_ROOT).getRequestDispatcher(request);

        dispatcher.dispatch(request, response, Thread.currentThread().getContextClassLoader());


        assertNotEquals(out.getBuffer().toString(), "");
        log.info(out.getBuffer());

    }
}