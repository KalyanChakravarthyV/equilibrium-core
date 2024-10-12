package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.NamingManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.Hashtable;

import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseOperationsAPITest {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPITest.class);


    protected void mockInitialContext() throws NamingException, SQLException {
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
    void execute() throws IOException, ServletException, SQLException, NamingException {


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/dataExport");

        String requestPayload = "{\"action\":\"dbExport\", \"body\" :\"q1:SELECT * FROM IBS_SPEC;\"}";
        BufferedReader requestStringReader = new BufferedReader(new StringReader(requestPayload));


        when(request.getReader()).thenReturn(requestStringReader);



        ServletOutputStream mockSos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(mockSos);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader).getRequestDispatcher(request);

        mockInitialContext();
        dispatcher.dispatch(request, response, currentClassLoader);

    }
}

