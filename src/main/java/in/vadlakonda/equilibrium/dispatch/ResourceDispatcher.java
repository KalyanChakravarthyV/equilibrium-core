package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceDispatcher implements RequestDispatcher {

    private static final String INDEX_HTML = "index.html";
    private Dispatcher dispatcher;

    private static final MimetypesFileTypeMap MIMETYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();
    private static final org.apache.log4j.Logger log = Logger.getLogger(ResourceDispatcher.class);

    public ResourceDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static MimetypesFileTypeMap getMimeTypeMap(){
        return  MIMETYPES_FILE_TYPE_MAP;
    }
    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws ServletException, IOException {

        String baseURI = dispatcher.getDispatcherConfig().getBaseURI();

        String resourceURI = StringUtils.substringAfter(request.getRequestURI(), baseURI);

        String resourceRoot = dispatcher.getResourceRoot();
        resourceURI = StringUtils.substringAfter(resourceURI, resourceRoot);

        String resourcePath = resourceRoot + File.separator + resourceURI;

        //TODO Fix this bug
        if(resourcePath.endsWith("/") || resourcePath.endsWith("/sql")
                || resourcePath.endsWith("/finder")
                || resourcePath.endsWith("/script")){
            resourcePath += INDEX_HTML;
            log.debug("Redirecting to:"+resourcePath);

        }
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);


        response.setContentType(MIMETYPES_FILE_TYPE_MAP.getContentType(resourcePath));
        log.debug("Serving:"+resourcePath+" as "+MIMETYPES_FILE_TYPE_MAP.getContentType(resourcePath));

        IOUtils.copy(inputStream, response.getWriter(), StandardCharsets.UTF_8);

    }
}
