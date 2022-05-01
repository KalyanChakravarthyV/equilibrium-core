package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.vadlakonda.equilibrium.api.config.API;
import in.vadlakonda.equilibrium.api.config.APIConfig;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class EquilibriumAPIFactory {

    private static final Logger log = Logger.getLogger(EquilibriumAPIFactory.class);
    private static final String ERROR_RESPONSE = "ERROR_RESPONSE";

    private static APIConfig apiConfig = null;

    private static final HashMap<String, EquilibriumAPI> API_OPERATION_MAP = new HashMap<String, EquilibriumAPI>();

    private static EquilibriumAPIFactory equilibriumAPIFactory = new EquilibriumAPIFactory();

    public static EquilibriumAPIFactory getEquilibriumAPIFactory(String apiConfigJsonFile) {

        if (apiConfig != null && !apiConfig.getApis().isEmpty()) return equilibriumAPIFactory;

        log.info("Initializing API Config");
        Gson gson = new Gson();

        Reader reader = null;
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(apiConfigJsonFile);

        if (inputStream == null) {
            log.error("Could not read:" + apiConfigJsonFile);
            log.error("Error Initializing API Config");
            return null;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));

        apiConfig = gson.fromJson(reader, new TypeToken<APIConfig>() {
        }.getType());

        for (API api : apiConfig.getApis()) {

            Class<? extends EquilibriumAPI> equilibriumAPIClass = null;
            try {
                equilibriumAPIClass = (Class<? extends EquilibriumAPI>) Class.forName(api.getApiClass());

                EquilibriumAPI equilibriumAPI = equilibriumAPIClass.newInstance();

                api.setApi(equilibriumAPI);

                API_OPERATION_MAP.put(api.getApiOperation(), equilibriumAPI);

                log.info(String.format("Adding API for [%s:%s] ", api.getApiOperation(), api.getApiClass()));

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log.error("Error Initializing API Config", e);

            }

        }
        log.info("Initialized API Config");

        return equilibriumAPIFactory;
    }


    public EquilibriumAPI getEquilibriumAPI(HttpServletRequest request) {

        String baseURI = apiConfig.getBaseURI();

        String operationURI = StringUtils.substringAfter(request.getRequestURI(), baseURI);


        String matchedResourceURI = API_OPERATION_MAP.keySet().stream().filter(e -> operationURI.startsWith(e)).findFirst().orElse(ERROR_RESPONSE);

        log.info(String.format("Operation URI[%s] - Matches:%s", operationURI, matchedResourceURI));

        if (matchedResourceURI == null)
            return API_OPERATION_MAP.get(ERROR_RESPONSE);

        return API_OPERATION_MAP.get(matchedResourceURI);
    }
}
