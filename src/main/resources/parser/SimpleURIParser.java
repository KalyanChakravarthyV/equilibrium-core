package in.vadlakonda.equilibrium.api.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import in.vadlakonda.equilibrium.dispatch.RequestDispatcher;
import in.vadlakonda.equilibrium.dispatch.config.Dispatcher;
import in.vadlakonda.equilibrium.dispatch.config.DispatcherConfig;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SimpleURIParser implements URIParser {

    private DispatcherConfig dispatcherConfig = new DispatcherConfig();

    public SimpleURIParser(String dispatchConfigJsonFile) {

        if (!dispatcherConfig.getDispatchers().isEmpty()) return;

        Gson gson = new Gson();

        Reader reader = null;
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dispatchConfigJsonFile);

        reader = new BufferedReader(new InputStreamReader(inputStream));

        dispatcherConfig = gson.fromJson(reader, new TypeToken<DispatcherConfig>() {
        }.getType());

        for (Dispatcher dispatcher : dispatcherConfig.getDispatchers()
        ) {

            Class<? extends RequestDispatcher> requestDispatcherclass = null;
            try {
                requestDispatcherclass = (Class<? extends RequestDispatcher>) Class.forName(dispatcher.getDispatcher());
                dispatcher.setRequestDispatcher(requestDispatcherclass.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public DispatcherConfig parseURI(HttpServletRequest request) {
        return null;
    }

    public static void main(String[] args) {
        new SimpleURIParser("dispatcher-config.json");
    }

}
