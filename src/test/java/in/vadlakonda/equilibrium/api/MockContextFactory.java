package in.vadlakonda.equilibrium.api;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

import static org.mockito.Mockito.mock;

public class MockContextFactory implements InitialContextFactory {
    private static InitialContext mockInitialContext = mock(InitialContext.class);

    @Override
    public Context getInitialContext(Hashtable<?, ?> hshtbl) throws NamingException {
        return mockInitialContext;
    }
}
