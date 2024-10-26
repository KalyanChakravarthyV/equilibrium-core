package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import in.vadlakonda.equilibrium.mock.servlet.MockHttpServletResponse;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;

import static in.vadlakonda.equilibrium.TestConstants.APP_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class DatabaseOperationsAPITest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPITest.class);


    @BeforeAll
    protected static void mockInitialContext() throws NamingException, SQLException {

        log.info("Initializing Mock Context Factory");
        System.setProperty("java.naming.factory.initial", "in.vadlakonda.equilibrium.api.MockContextFactory");

        InitialContext mockInitialContext = (InitialContext) NamingManager.getInitialContext(System.getProperties());
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        ResultSet mockResultSet = mock(ResultSet.class);

        ResultSetMetaData mockResultSetMetaData = mock(ResultSetMetaData.class);


        when(mockInitialContext.lookup(anyString())).thenReturn(mockDataSource);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.getResultSet()).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockResultSetMetaData);

        when(mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getString(anyString())).thenReturn((new Date(System.currentTimeMillis())).toString());
        when(mockResultSet.next()).thenReturn(false);


        try {
            when(mockDataSource.getConnection()).thenReturn(mockConnection);
        } catch (SQLException ex) {
            log.error("Error with Initial Context lookup", ex);
        }
    }

    @Test
    void testExport() throws IOException, ServletException, SQLException, NamingException {


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(MockHttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/dataExport");

        String requestPayload = "{\"action\":\"export\", \"body\" :\"q1:SELECT * FROM IBS_SPEC;\"}";
        BufferedReader requestStringReader = new BufferedReader(new StringReader(requestPayload));


        when(request.getReader()).thenReturn(requestStringReader);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);

        ServletOutputStream mockSos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(mockSos);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader, APP_ROOT).getRequestDispatcher(request);

        doCallRealMethod().when(response).setContentType(any(String.class));
        dispatcher.dispatch(request, response, currentClassLoader);
        when(response.getContentType()).thenCallRealMethod();

        log.info("Response Content Type:"+ response.getContentType());
        log.info("Response Content Length:"+ response.getHeader("Content-Length"));


    }

    @Test
    void testUpdate() throws IOException, ServletException, SQLException, NamingException {


        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = mock(MockHttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/dataExport");

        String requestPayload = "{\"action\":\"update\", \"body\" :\"q1:UPDATE T_TRIPATCHHELPER SET SPEC_ID = - SPEC_ID WHERE SPEC_ID <0;\"}";
        BufferedReader requestStringReader = new BufferedReader(new StringReader(requestPayload));

        when(request.getReader()).thenReturn(requestStringReader);
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        when(response.getWriter()).thenReturn(writer);

        ServletOutputStream mockSos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(mockSos);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader, APP_ROOT).getRequestDispatcher(request);

        doCallRealMethod().when(response).setContentType(any(String.class));
        dispatcher.dispatch(request, response, currentClassLoader);

        //It's not the real Method and hence null
        assertNull(response.getContentType());
        when(response.getContentType()).thenCallRealMethod();

        //Now it's not the real Method and hence the value
        assertEquals("application/json", response.getContentType());


    }
}

