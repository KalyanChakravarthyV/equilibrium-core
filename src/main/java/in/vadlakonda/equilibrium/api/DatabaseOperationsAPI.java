package in.vadlakonda.equilibrium.api;

import in.vadlakonda.equilibrium.api.request.Payload;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.naming.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseOperationsAPI extends AbstractAPI {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPI.class);
    private static final String SQL_SHEET_NAME = "SQL Queries";

    public static Map toMap(Context ctx) throws NamingException {
        String namespace = ctx instanceof InitialContext ? ctx.getNameInNamespace() : "";
        HashMap<String, Object> map = new HashMap<String, Object>();
        log.info("> Listing namespace: " + namespace);
        NamingEnumeration<NameClassPair> list = ctx.list(namespace);
        while (list.hasMoreElements()) {
            NameClassPair next = list.next();
            String name = next.getName();
            String jndiPath = namespace + name;
            Object lookup;
            try {
                log.debug("Looking up name: " + jndiPath);
                Object tmp = ctx.lookup(jndiPath);
                if (tmp instanceof Context) {
                    lookup = toMap((Context) tmp);
                } else {
                    lookup = tmp.toString();
                }
            } catch (Throwable t) {
                lookup = t.getMessage();
            }
            map.put(name, lookup);

        }
        return map;
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException {


        this.initializeAndValidate(request, response);

        try {
            if (payload.getAction().equals("export")) {
                dbExport(payload, response);
            } else if (payload.getAction().equals("update")) {
                dbUpdate(payload, response);
            }
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new APIException(500, e.getMessage());
        }

    }

    private void dbUpdate(Payload payload, HttpServletResponse response) throws IOException, SQLException, NamingException {

        log.info("Executing Updates");
        String queryBody = payload.getBody();

        response.setContentType("application/json");

        StringBuffer responsetoPrint = new StringBuffer();

        StringTokenizer queryTokenizer = new StringTokenizer(queryBody, ";");

        while (queryTokenizer.hasMoreTokens()) {
            String nextToken = queryTokenizer.nextToken();
            log.debug("SQL Update->" + nextToken);

            StringTokenizer queryNameTokenizer = new StringTokenizer(nextToken, ":");

            String queryName = "q1";

            if(queryNameTokenizer.hasMoreTokens())
                queryName = queryNameTokenizer.nextToken();

            String query = "SELECT '' FROM DUAL";

            if(queryNameTokenizer.hasMoreTokens())
                query = queryNameTokenizer.nextToken();



            String sqlResult = "";

            Connection connection = getConnection();
            PreparedStatement preparedStatement = null;

            try {
                preparedStatement = connection.prepareStatement(query);
                sqlResult = String.valueOf(preparedStatement.execute());


                responsetoPrint.append(String.format("{\"%s\":\"%s\"},", queryName, sqlResult));
                preparedStatement.close();

            } catch (SQLException e) {
                sqlResult = e.getMessage();
                responsetoPrint.append(String.format("{\"%s\":\"%s\"},", queryName, sqlResult));
            }

            connection.close();

        }


        response.getWriter().println(String.format("{\"result\":[%s]}", responsetoPrint));

    }

    private void dbExport(Payload payload, HttpServletResponse response) throws IOException, APIException {

        log.info("Executing Export");

        String queryBody = payload.getBody();


        StringTokenizer queryTokenizer = new StringTokenizer(queryBody, ";");

        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();

        List<String> sqlList = new LinkedList<String>();

        int queryCounter = 0;

        while (queryTokenizer.hasMoreTokens()) {
            String nextToken = queryTokenizer.nextToken();
            log.debug("SQL Export->" + nextToken);

            String queryName = "q"+queryCounter++;
            String query = "SELECT '' FROM DUAL";

            if(nextToken.contains(":")) {
                StringTokenizer queryNameTokenizer = new StringTokenizer(nextToken, ":");

                if (queryNameTokenizer.hasMoreTokens())
                    queryName = queryNameTokenizer.nextToken();

                if (queryNameTokenizer.hasMoreTokens())
                    query = queryNameTokenizer.nextToken();
            }else
                query = nextToken;


            sqlList.add(queryName+':'+query);


            log.info(queryName+':'+query);
            Sheet sheet = workbook.createSheet(queryName);

            fillSheetWithData(sheet, query);


        }
        appendSQLs(workbook, sqlList);

        response.setContentType("application/vnd.ms-excel");


        response.addHeader("Content-disposition", "attachment;filename=DataExport.xlsx");

        workbook.write(response.getOutputStream());
    }

    private boolean appendSQLs(Workbook workbook, List<String> sqlList) throws APIException {

        Sheet sheet = workbook.createSheet(SQL_SHEET_NAME);
        int rowNum = 0, col = 0;
        for (String sql : sqlList
        ) {
            XSSFRow sheetRow = (XSSFRow) sheet.createRow(rowNum++);

            Cell cell = sheetRow.createCell(col);
            cell.setCellValue(sql);

        }
        return true;
    }

    private boolean fillSheetWithData(Sheet workSheet, String query) throws APIException {

        Connection connection = null;
        ResultSet resultSet = null;
        ResultSetMetaData resultSetMetaData = null;

        try {

            connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeQuery();

            resultSet = preparedStatement.getResultSet();
            resultSetMetaData = resultSet.getMetaData();

            fillSheetWithHeader(workSheet, resultSetMetaData);

            int rowNum = 1, col = 0, colCount = resultSetMetaData.getColumnCount();

            log.debug("Excel Export Initiating");

            while (resultSet.next()) {
                XSSFRow sheetRow = (XSSFRow) workSheet.createRow(rowNum++);
                col = 0;

                while (col < colCount) {
                    Cell cell = sheetRow.createCell(col);
                    cell.setCellValue(resultSet.getString(col + 1));
                    col++;
                }
            }

            preparedStatement.close();
            resultSet.close();
            connection.close();

        } catch (SQLException e) {
            log.error("SQLException Occurred:", e);
            throw new APIException(500, e.getMessage());

        } catch (NamingException e) {
            log.error("NamingException Occurred:", e);

            throw new APIException(500, e.getMessage());
        }

        log.debug("Excel Sheet Populated");


        return resultSetMetaData != null;


    }

    private void fillSheetWithHeader(Sheet workSheet, ResultSetMetaData resultSetMetaData) throws SQLException {

        int col = 0, colCount = resultSetMetaData.getColumnCount();

        Row r = workSheet.createRow(0);

        while (col < colCount) {
            r.createCell(col).setCellValue(resultSetMetaData.getColumnName(col + 1));
            col++;
        }
    }

    private Connection getConnection() throws NamingException, SQLException {


        javax.sql.DataSource dataSource = null;

        Context context = new InitialContext(new Hashtable<>());

        log.info("Context :" + context.getClass());
//        try {
        dataSource = (javax.sql.DataSource) context.lookup("jdbc/local/DataSource-TRIRIGA-data");
        log.info("Found:" + "jdbc/local/Datasource-TRIRIGA-data");
//        } catch (NamingException e) {
//            try {
//                dataSource = (DataSource) context.lookup("java:jdbc/local/Datasource-TRIRIGA-data");
//                log.info("Found:" + "java:jdbc/local/Datasource-TRIRIGA-data");
//
//            } catch (NamingException e2) {
//
//                dataSource = (DataSource) context.lookup("java:comp/env/jdbc/local/Datasource-TRIRIGA-data");
//                log.info("Found:" + "java:comp/env/jdbc/local/Datasource-TRIRIGA-data");
//
//            }
//
//
//        }
        return dataSource.getConnection();

    }
}
