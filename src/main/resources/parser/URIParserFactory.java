package in.vadlakonda.equilibrium.api.parser;

public class URIParserFactory {


    private static URIParserFactory uriParserFactory = new URIParserFactory();


    public static URIParserFactory getRequestDispatcherFactory(){
        return uriParserFactory;
    }

    public URIParser getURIParser(String uriConfigJson){

        uriConfigJson = uriConfigJson == null || uriConfigJson.isEmpty() ? "dispatcher-config.json" : uriConfigJson;

        return new SimpleURIParser(uriConfigJson);
    }


}
