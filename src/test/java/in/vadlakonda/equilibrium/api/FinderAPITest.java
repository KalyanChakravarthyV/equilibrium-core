package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import in.vadlakonda.equilibrium.TestConstants;
import in.vadlakonda.equilibrium.api.response.FinderResponse;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import in.vadlakonda.equilibrium.mock.servlet.MockHttpServletResponse;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.stream.Stream;

import static in.vadlakonda.equilibrium.TestConstants.APP_ROOT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class FinderAPITest {

    private static final Logger log = Logger.getLogger(FinderAPITest.class);

    @Order(1)
    @ParameterizedTest
    @MethodSource("provideArgumentsForUserFiles")
    void testList(TestConstants.FileType fileType, Integer pathID, int dirCount, int fileCount) throws IOException, ServletException{


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(MockHttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/finder");

        String requestPayload = "{\"action\":\"list\", \"body\" : \"path:"+pathID+"\"}";
        BufferedReader requestStringReader = new BufferedReader(new StringReader(requestPayload));


        when(request.getReader()).thenReturn(requestStringReader);

        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);

        ServletOutputStream mockSos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(mockSos);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader, APP_ROOT).getRequestDispatcher(request);


        if(fileType.equals(TestConstants.FileType.DIR)) {

            doCallRealMethod().when(response).setContentType(any(String.class));

            dispatcher.dispatch(request, response, currentClassLoader);

            FinderResponse finderResponse = new Gson().fromJson(out.toString(), FinderResponse.class);

            assertNotNull(finderResponse);

            assertEquals(dirCount, finderResponse.getDirectories().size());
            assertEquals(fileCount, finderResponse.getFiles().size());
            log.info("JSON Response:"+out.toString());

        }else {


            doCallRealMethod().when(response).setContentType(any(String.class));
            dispatcher.dispatch(request, response, currentClassLoader);

            assertNotNull(out.toString());

            when(response.getContentType()).thenCallRealMethod();

            assertEquals("text/plain", response.getContentType());
            log.info("File Contents:"+out.toString());

        }

        assertNotNull(out.toString());


    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForUserFiles")
    void testDownload(TestConstants.FileType fileType, Integer filePathID, int fileItemCount) throws IOException, ServletException, SQLException, NamingException {



        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRequestURI()).thenReturn("/html/en/default/rest/Equilibrium/api/finder");

        String requestPayload = "{\"action\":\"download\", \"body\" :\"path"+filePathID+"\"}";
        BufferedReader requestStringReader = new BufferedReader(new StringReader(requestPayload));

        when(request.getReader()).thenReturn(requestStringReader);
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        when(response.getWriter()).thenReturn(writer);

        ServletOutputStream mockSos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(mockSos);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        RequestDispatcher dispatcher = RequestDispatcherFactory.getRequestDispatcherFactory("dispatcher-config.json", currentClassLoader, APP_ROOT).getRequestDispatcher(request);

        dispatcher.dispatch(request, response, currentClassLoader);

        assertNotNull( mockSos);

    }

    private static Stream<Arguments> provideArgumentsForUserFiles() {
        return Stream.of(
                //path, directoryCount, fileCount
                Arguments.of(TestConstants.FileType.DIR,"-root-".hashCode(), 2, 2),// userfiles, log4j.properties, plain-text.txt
                Arguments.of(TestConstants.FileType.DIR, "userfiles/ClassLoader".hashCode(), 2, 0),
                Arguments.of(TestConstants.FileType.FILE, "plain-text.txt".hashCode(), 0, 0)
        );
    }
}



