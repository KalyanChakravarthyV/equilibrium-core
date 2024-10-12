package in.vadlakonda.equilibrium.dispatch;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceDispatcherTest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(ResourceDispatcherTest.class);
    private static URLClassLoader classLoader = null;


    @BeforeAll
    public static void setup() throws MalformedURLException {

        File appRoot = new File("userfiles", "ClassLoader/Equilibrium");

        File jarRoot = new File(appRoot, "Java");
        File webRoot = new File(appRoot, "Web");

        List<URL> urlList = new ArrayList<URL>();

        for (File f : jarRoot.listFiles()) {
            try {
                urlList.add(f.toPath().toUri().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        urlList.add(webRoot.toPath().toUri().toURL());


        /*
        for(File f: webRoot.listFiles()){
            try {
                if(f.isDirectory()) urlList.add(f.toPath().toUri().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
         */
        classLoader = new URLClassLoader(urlList.toArray(new URL[]{}));
    }

    @Test
    public void testResource() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/resource/dashboard/");


        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);


        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", classLoader)
                .getRequestDispatcher(request);

        dispatcher.dispatch(request, response, classLoader);

        log.info(out.getBuffer());
    }

}