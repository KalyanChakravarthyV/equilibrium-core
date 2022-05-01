package in.vadlakonda.equilibrium.api;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.naming.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

public class DatabaseOperationsAPI implements EquilibriumAPI {

    private static final org.apache.log4j.Logger log = Logger.getLogger(DatabaseOperationsAPI.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException {


        BufferedReader bufferedReader = null;

        try {
//            BufferedReader reader  = request.getReader();
//            if (reader != null) {
//                Gson gson = new Gson();

//                Payload payload = gson.fromJson(reader, Payload.class);


            String queryBody = request.getParameter("body");


            StringTokenizer queryTokenizer = new StringTokenizer(queryBody, ";");

            Workbook workbook = new XSSFWorkbook();
            CreationHelper createHelper = workbook.getCreationHelper();

            while (queryTokenizer.hasMoreTokens()) {
                String nextToken = queryTokenizer.nextToken();

                StringTokenizer queryNameTokenizer = new StringTokenizer(nextToken, ":");

                String queryName = queryNameTokenizer.nextToken();

                String query = queryNameTokenizer.nextToken();

                log.info(String.format("%s:%s", queryName, query));

                Sheet sheet = workbook.createSheet(queryName);

                fillSheetWithData(sheet, query);


            }

            response.setContentType("application/vnd.ms-excel");


            response.addHeader("Content-disposition", "attachment;filename=DataExport");

            workbook.write(response.getOutputStream());


        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new APIException(500, e.getMessage());

        }

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

            while (resultSet.next()) {
                XSSFRow sheetRow = (XSSFRow) workSheet.createRow(rowNum++);
                col = 0;
                while (col < colCount) {
                    Cell cell = sheetRow.createCell(col);
                    cell.setCellValue(resultSet.getString(col + 1));
                    col++;
                }

            }
        } catch (SQLException e) {
            throw new APIException(500, e.getMessage());


        } catch (NamingException e) {
            log.error("NamingException Occurred:", e);

            throw new APIException(500, e.getMessage());
        }


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

        log.info("Map:" + toMap(context));

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
                log.info("> Looking up name: " + jndiPath);
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
}
