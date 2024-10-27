package in.vadlakonda.equilibrium.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import in.vadlakonda.equilibrium.api.request.Payload;
import in.vadlakonda.equilibrium.api.response.ScriptEngineResponse;
import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

public class ScriptEngineAPI extends AbstractAPI {
    private static final Logger log = Logger.getLogger(ScriptEngineAPI.class);
    private static final ScriptEngineManager manager = new ScriptEngineManager();
    private static final ScriptEngine engine = manager.getEngineByName("nashorn");
    private static final String TRIRIGA_WS_SESSION_KEY = "TririgaWS";
    private static final String TRIRIGA_WS_BINDING_NAME = "tririgaWS";
    private static final Gson GSON = GSON_BUILDER.create();
    private static final String GSON_BINDING_NAME = "GSON";



    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, ClassLoader classLoader) throws APIException, IOException {

        this.initializeAndValidate(request, response);
        //payload is read from the request
        try {
            executeScript(this.payload, request, response);
        } catch (APIException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("Error occurred", e);
            throw new APIException(HTTP_INTERNAL_ERROR, e.getMessage());
        }


    }

    private void executeScript(Payload payload, HttpServletRequest request, HttpServletResponse response) throws APIException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) throw new APIException(HttpURLConnection.HTTP_FORBIDDEN, "No active session available");

        Object tririgaWS = session.getAttribute(TRIRIGA_WS_SESSION_KEY);


        // set global variable
        engine.put(TRIRIGA_WS_BINDING_NAME, tririgaWS);
        engine.put(GSON_BINDING_NAME, GSON);

        Object out = null;
        ScriptEngineResponse scriptEngineResponse = new ScriptEngineResponse("","");


        try {


            out = engine.eval(payload.getBody());
            scriptEngineResponse.setOut(out+"");
            // define a different script context
            //ScriptContext newContext = new SimpleScriptContext();
            //newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);

            //Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
            // set the variable to a different value in another scope
            //engineScope.put("x", "world");

            // evaluate the same code but in a different script context (x = "world")
            //engine.eval(payload.getBody(), newContext);
        } catch (ScriptException s) {
            scriptEngineResponse.setErr(s.getMessage());

        }

        Gson gson = GSON_BUILDER.create();
        String jsonOutput = gson.toJson(scriptEngineResponse);
        response.setContentType("application/json");
        response.getWriter().write(jsonOutput);

        log.debug("Script Execution Completed");

    }
}
