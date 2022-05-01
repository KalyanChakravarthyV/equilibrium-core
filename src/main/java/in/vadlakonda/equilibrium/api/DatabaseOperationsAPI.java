package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.vadlakonda.equilibrium.api.config.APIConfig;
import in.vadlakonda.equilibrium.api.request.Payload;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
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

                Sheet workSheet = getSheetWithData(query, queryName, workbook);

                workSheet.createRow(1).createCell(1).setCellValue(query);
            }

            response.setContentType("application/vnd.ms-excel");


            response.addHeader("Content-disposition", "attachment;filename=DataExport");

            workbook.write(response.getOutputStream());


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Sheet getSheetWithData(String query, String sheetName, Workbook workbook) {

        Connection connection = getConnection();

        Sheet sheet = workbook.createSheet(sheetName);

        return sheet;



    }

    private Connection getConnection() {

        return null;

    }

}
