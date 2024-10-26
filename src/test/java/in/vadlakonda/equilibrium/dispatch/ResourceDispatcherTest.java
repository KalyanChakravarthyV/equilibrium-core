package in.vadlakonda.equilibrium.dispatch;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceDispatcherTest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(ResourceDispatcherTest.class);
    private static URLClassLoader classLoader = null;


    @BeforeAll
    public static void setup() throws MalformedURLException {

        File appRoot = new File("build/resources/test", "userfiles/ClassLoader/Equilibrium");

        File jarRoot = new File(appRoot, "Java");
        File webRoot = new File(appRoot, "Web");

        List<URL> urlList = new ArrayList<URL>();


        if (jarRoot.isDirectory())
            for (File f : jarRoot.listFiles()) {
                try {
                    urlList.add(f.toPath().toUri().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

        log.info("webRoot.listFiles()" + webRoot.listFiles());
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

    @ParameterizedTest
    @ValueSource(strings = {"/html/en/default/rest/Equilibrium/app/main/index.html", "/html/en/default/rest/Equilibrium/app/main/manifest.json"})
    public void testResource(String resourcePath) throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn(resourcePath);


        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);


        RequestDispatcherFactory requestDispatcherFactory = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", classLoader, new File("./test/resource/"));
        assertNotNull(requestDispatcherFactory);
        RequestDispatcher dispatcher = requestDispatcherFactory.getRequestDispatcher(request);
        assertNotNull(dispatcher);

        dispatcher.dispatch(request, response, classLoader);

        log.info(out.getBuffer());
    }

}