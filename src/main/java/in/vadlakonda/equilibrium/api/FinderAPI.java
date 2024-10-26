package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.vadlakonda.equilibrium.api.request.Payload;
import in.vadlakonda.equilibrium.api.response.Directory;
import in.vadlakonda.equilibrium.api.response.FinderResponse;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory;
import in.vadlakonda.equilibrium.dispatch.ResourceDispatcher;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static in.vadlakonda.equilibrium.dispatch.RequestDispatcherFactory.APP_ROOT;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class FinderAPI extends AbstractAPI {
    private static final org.apache.log4j.Logger log = Logger.getLogger(FinderAPI.class);
    private static final int BUFFER = 1024;
    private static final HashMap<String, String> PATH_HASHCODE_MAP = new HashMap<String, String>();
    private static final String ROOT_PATH = "-root-";
    private static int RELATIVE_PATH_INDEX;

    static {
        try {
            RELATIVE_PATH_INDEX = APP_ROOT.getCanonicalPath().length();
        } catch (IOException e) {
            log.error("Error initializing RELATIVE_PATH_INDEX:" + e.getMessage(), e);
        }
    }

    ;


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException {

        this.initializeAndValidate(request, response);

        //payload is read from the request
        try {
            execute(this.payload, response);
        } catch (APIException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new APIException(HTTP_INTERNAL_ERROR, e.getMessage());
        }

    }

    private void execute(Payload payload, HttpServletResponse response) throws IOException, APIException {

        if (PATH_HASHCODE_MAP.isEmpty()) intializeHashCodeMap();
        String action = payload.getAction();

        StringTokenizer pathTokenizer = new StringTokenizer(payload.getBody(), ":");

        //For the prefix 'path':
        if (pathTokenizer.hasMoreTokens())
            pathTokenizer.nextToken();
        else
            throw new APIException(HTTP_BAD_REQUEST, "Request payload.body needs to have 'path:<path>'");

        String pathID = pathTokenizer.hasMoreTokens() ? pathTokenizer.nextToken() : ROOT_PATH;

        String path = PATH_HASHCODE_MAP.get(pathID);

        if (path == null) path = ROOT_PATH;

        switch (action) {
            case "list":
            case "view":
                list(payload, response, path);
                break;
            case "download":
                download(payload, response, path);
        }
    }

    private void intializeHashCodeMap() throws IOException {
        int relativePathIndx = RELATIVE_PATH_INDEX;

        PATH_HASHCODE_MAP.put(ROOT_PATH.hashCode() + "", ROOT_PATH);
        fillHasCode(APP_ROOT, relativePathIndx, false);

        log.debug("PATH_HASHCODE_MAP:"+PATH_HASHCODE_MAP);
    }

    private void fillHasCode(File dir, int relativePathIndx, boolean recursive) throws IOException {


        String relativePath = dir.getCanonicalPath().substring(recursive ? relativePathIndx + 1 : relativePathIndx).replace("\\", "/");
        PATH_HASHCODE_MAP.put(relativePath.hashCode() + "", relativePath);

        for (File f : dir.listFiles()) {
            if (f.isDirectory())
                fillHasCode(f, relativePathIndx, true);
            else if (f.isFile()) {
                String relativeFilePath = f.getCanonicalPath().substring(relativePathIndx + 1).replace("\\", "/");
                PATH_HASHCODE_MAP.put(relativeFilePath.hashCode() + "", relativeFilePath);
            }
        }

    }

    private String relativeToAppRoot(File f) throws IOException {

        if (APP_ROOT.getCanonicalPath().equals(f.getCanonicalPath()))
            return ROOT_PATH;
        else
            return f.getCanonicalPath().substring(RELATIVE_PATH_INDEX + 1).replace("\\", "/");

    }

    private void download(Payload payload, HttpServletResponse response, String filePath) throws IOException {

        File currentFile = new File(APP_ROOT, filePath);

        if (currentFile.isFile() && currentFile.canRead()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + currentFile.getName() + "\"");

            OutputStream out = response.getOutputStream();

            Path path = currentFile.toPath();
            Files.copy(path, out);
            out.flush();

        } else if (currentFile.isDirectory() && currentFile.canRead()) {

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + currentFile.getName() + ".zip\"");


            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));


            byte data[] = new byte[BUFFER];

            File currentDir = currentFile;

            File[] files = currentDir.listFiles();

            for (File f : files) {

                if (f.isDirectory()) continue;

                FileInputStream fi = new FileInputStream(f);
                BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(currentDir.getName() + File.separator + f.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                    out.flush();
                }
                origin.close();
            }
            out.close();
        }

    }


    private void list(Payload payload, HttpServletResponse response, String path) throws IOException {


        File pathList = new File(APP_ROOT, path.equals(ROOT_PATH) ? "." : path);

        log.debug("Listing:" + pathList.getAbsolutePath());
        if (pathList.isDirectory()){

            FinderResponse finderResponse = listDir(payload,response,pathList);
            Gson gson = GSON_BUILDER.create();
            String jsonOutput = gson.toJson(finderResponse);
            response.setContentType("application/json");
            response.getWriter().write(jsonOutput);
        } else if (pathList.isFile()){

            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(pathList));

            String responseContentType =
                    ResourceDispatcher.getMimeTypeMap().getContentType(pathList.getName());

            //log.debug("Response Content-type:"+responseContentType);
            response.setContentType(responseContentType);

            IOUtils.copy(inputStream, response.getWriter(), StandardCharsets.UTF_8);


        }
    }

    private FinderResponse listFile(Payload payload, HttpServletResponse response, File file) throws IOException {


        FinderResponse finderResponse = new FinderResponse();

        in.vadlakonda.equilibrium.api.response.File responseFile = new in.vadlakonda.equilibrium.api.response.File(file.getName(), file.hashCode());

        responseFile.setSize(file.length());
        responseFile.setLastModified(new Date(file.lastModified()));
        finderResponse.getFiles().add(responseFile);

        return finderResponse;
    }
        private FinderResponse listDir(Payload payload, HttpServletResponse response, File dir) throws IOException {
            FinderResponse finderResponse = new FinderResponse();


            Directory parentDir = new Directory("..", relativeToAppRoot(dir.getParentFile()).hashCode());
            parentDir.setLastModified(new Date(dir.getParentFile().lastModified()));
            parentDir.setPath(relativeToAppRoot(dir.getParentFile()));
            finderResponse.getDirectories().add(parentDir);

            for (File f : dir.listFiles()) {

                if (f.isDirectory()) {
                    Directory d = new Directory(f.getName(), relativeToAppRoot(f).hashCode());
                    d.setLastModified(new Date(f.lastModified()));
                    d.setPath(relativeToAppRoot(f));

                    finderResponse.getDirectories().add(d);

                } else if (f.isFile()) {

                    in.vadlakonda.equilibrium.api.response.File responseFile = new in.vadlakonda.equilibrium.api.response.File(f.getName(), relativeToAppRoot(f).hashCode());

                    responseFile.setSize(f.length());
                    responseFile.setLastModified(new Date(f.lastModified()));
                    responseFile.setPath(relativeToAppRoot(f));
                    finderResponse.getFiles().add(responseFile);

                }

            }
            return  finderResponse;

    }
}
