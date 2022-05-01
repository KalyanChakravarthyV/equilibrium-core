package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.mockito.Mockito.when;

class DatabaseOperationsAPITest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPITest.class);

    @Test
    void execute() throws IOException, ServletException {


        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/dataExport");

        String requestPayload = "{\"action\":\"dataExport\", \"body\" :\"SELECT * FROM IBS_SPEC\"}";
        BufferedReader stringReader = new BufferedReader(new StringReader(requestPayload));


        when(request.getReader()).thenReturn(stringReader);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json").getRequestDispatcher(request);

        dispatcher.dispatch(request, response, Thread.currentThread().getContextClassLoader());
        log.info(out.getBuffer());


    }
}