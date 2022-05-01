package in.vadlakonda.equilibrium.api.parser;

import in.vadlakonda.equilibrium.dispatch.config.DispatcherConfig;

import javax.servlet.http.HttpServletRequest;

public interface URIParser {

    public DispatcherConfig parseURI(HttpServletRequest request);
}
