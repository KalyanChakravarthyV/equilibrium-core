package in.vadlakonda.equilibrium.dispatch;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestDispatcherFactoryTest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(RequestDispatcherFactoryTest.class);

    //    @Mock
    private static HttpServletRequest request;

    private static RequestDispatcherFactory factory;

    @BeforeAll
    static void init() {

        request = mock(HttpServletRequest.class);


    }

    @org.junit.jupiter.api.Test()
    @Order(1)
    void getRequestDispatcherFactory() {

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        assertNotNull((this.factory = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader)));
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(strings = {"/html/en/default/rest/Equilibrium/api/dataExport", "/html/en/default/rest/Equilibrium/api/runKts",
            "/html/en/default/rest/Equilibrium/api/processBuilder", "/html/en/default/rest/Equilibrium/api/currentTime"})
    void getAPIRequestDispatcher(String uri) {

        assertNotNull(this.factory);
        when(request.getRequestURI()).thenReturn(uri);

        assertEquals(factory.getRequestDispatcher(request).getClass(), APIDispatcher.class);
    }


    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = {"/something", "/html/en/default/rest/Equilibrium/somethingElse",
            "/html/en/default/rest/Equilibrium/res/Test"})
    void getErrorResponseDispatcher(String uri) {

        log.info("Testing against:" + uri);
        assertNotNull(this.factory);
        when(request.getRequestURI()).thenReturn(uri);

        assertEquals(factory.getRequestDispatcher(request).getClass(), ErrorResponseDispatcher.class);
    }

}