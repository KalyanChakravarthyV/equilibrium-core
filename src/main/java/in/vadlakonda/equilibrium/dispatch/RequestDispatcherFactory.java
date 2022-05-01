package in.vadlakonda.equilibrium.dispatch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;
import in.vadlakonda.equilibrium.dispatch.config.DispatcherConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RequestDispatcherFactory {

    private static final org.apache.log4j.Logger log = Logger.getLogger(RequestDispatcherFactory.class);
    private static final String ERROR_RESPONSE = "ERROR_RESPONSE";

    private static DispatcherConfig dispatcherConfig = null;

    private static final HashMap<String, RequestDispatcher> URI_DISPATCHER_MAP = new HashMap<String, RequestDispatcher>();

    private static RequestDispatcherFactory requestDispatcherFactory = new RequestDispatcherFactory();


    public static RequestDispatcherFactory getRequestDispatcherFactory(String dispatchConfigJsonFile, ClassLoader classLoader) {

        if (dispatcherConfig != null && !dispatcherConfig.getDispatchers().isEmpty()) return requestDispatcherFactory;

        log.info("Initializing DispatcherConfig");
        Gson gson = new Gson();

        Reader reader = null;
        InputStream inputStream = classLoader.getResourceAsStream(dispatchConfigJsonFile);

        if (inputStream == null) {
            log.error("Could not read:" + dispatchConfigJsonFile);
            log.error("Error Initializing DispatcherConfig");
            return null;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));

        dispatcherConfig = gson.fromJson(reader, new TypeToken<DispatcherConfig>() {
        }.getType());

        for (Dispatcher dispatcher : dispatcherConfig.getDispatchers()
        ) {

            Class<? extends RequestDispatcher> requestDispatcherclass = null;
            try {
                requestDispatcherclass = (Class<? extends RequestDispatcher>) Class.forName(dispatcher.getDispatcher());

                Constructor<? extends RequestDispatcher>[] constructors
                        = (Constructor<RequestDispatcher>[]) requestDispatcherclass.getConstructors();

                RequestDispatcher requestDispatcher = constructors[0].newInstance(dispatcher);

                dispatcher.setRequestDispatcher(requestDispatcher);

                URI_DISPATCHER_MAP.put(dispatcher.getResourceURI(), requestDispatcher);

                dispatcher.setDispatcherConfig(dispatcherConfig);

                log.info(String.format("Adding dispatcher for [%s:%s] ", dispatcher.getResourceURI(), dispatcher.getDispatcher()));

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log.error("Error Initializing DispatcherConfig", e);

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        log.info("Initialized DispatcherConfig");

        return requestDispatcherFactory;
    }


    public RequestDispatcher getRequestDispatcher(HttpServletRequest request) {

        String baseURI = dispatcherConfig.getBaseURI();

        String resourceURI = StringUtils.substringAfter(request.getRequestURI(), baseURI);


        String matchedResourceURI = URI_DISPATCHER_MAP.keySet().stream().filter(e -> resourceURI.matches(e)).findFirst().orElse(ERROR_RESPONSE);

        log.info(String.format("Resource URI[%s] - Matches:%s",resourceURI, matchedResourceURI));

        if(matchedResourceURI == null)
            return URI_DISPATCHER_MAP.get(ERROR_RESPONSE);

        return URI_DISPATCHER_MAP.get(matchedResourceURI);
    }
}
