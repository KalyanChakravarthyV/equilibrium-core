package in.vadlakonda.equilibrium.dispatch;

import in.vadlakonda.equilibrium.api.EquilibriumAPIFactory;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ResourceDispatcher implements RequestDispatcher{

    private Dispatcher dispatcher;

    public ResourceDispatcher(Dispatcher dispatcher){
        this.dispatcher = dispatcher;
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response,ClassLoader classLoader) throws ServletException, IOException {

        String baseURI = dispatcher.getDispatcherConfig().getBaseURI();

        String resourceURI = StringUtils.substringAfter(request.getRequestURI(), baseURI);

        String resourceRoot = dispatcher.getResourceRoot();
        resourceURI = StringUtils.substringAfter(resourceURI, resourceRoot);


        InputStream inputStream = classLoader.getResourceAsStream(resourceRoot+File.separator+resourceURI);

        IOUtils.copy(inputStream, response.getWriter(), StandardCharsets.UTF_8);

    }
}
