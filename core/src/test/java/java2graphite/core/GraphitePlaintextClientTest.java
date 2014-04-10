package java2graphite.core;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Before;
import org.junit.Test;

import javax.net.SocketFactory;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:ivan.mushketyk@gmail.com">Ivan Mushketyk</a>
 */
public class GraphitePlaintextClientTest {

    private GraphitePlaintextClient graphitePlaintextClient;
    private SocketFactory socketFactory;
    private Clock clock;
    private Socket socket;

    private static final String HOST = "testHost";
    private static final int PORT = 1234;

    private static final InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(HOST, PORT);
    private ByteOutputStream byteOutputStream;

    @Before
    public void before() throws Exception {
        socketFactory = mock(SocketFactory.class);
        clock = mock(Clock.class);
        socket = mock(Socket.class);

        graphitePlaintextClient = new GraphitePlaintextClient(SERVER_ADDRESS, socketFactory, clock);
        byteOutputStream = new ByteOutputStream();

        when(socketFactory.createSocket(anyString(), anyInt())).thenReturn(socket);
        when(socket.getOutputStream()).thenReturn(byteOutputStream);
    }

    @Test
    public void testGetServerAddress() {
        assertEquals(graphitePlaintextClient.getServerAddress(), SERVER_ADDRESS);
    }

    @Test
    public void testGetSocketFactory() {
        assertEquals(graphitePlaintextClient.getSocketFactory(), socketFactory);
    }

    @Test
    public void testGetClock() {
        assertEquals(graphitePlaintextClient.getClock(), clock);
    }

    @Test
    public void testConnect() throws Exception {
        graphitePlaintextClient.connect();
        verify(socketFactory).createSocket(HOST, PORT);
    }

    @Test
    public void testClose() throws Exception {
        graphitePlaintextClient.connect();
        graphitePlaintextClient.close();

        verify(socket).close();
    }

    @Test
    public void testSendInt() throws Exception {
        long timestamp = 2345;
        when(clock.currentTime()).thenReturn(timestamp);

        graphitePlaintextClient.connect();
        graphitePlaintextClient.send("a.b.c", 10);
        assertSent("a.b.c 10 2345");
    }

    private void assertSent(String... lines) {
        String expected = join(lines, '\n');
        String actual = new String(byteOutputStream.getBytes(), 0, byteOutputStream.getCount());

        assertEquals(actual, expected);
    }

    private String join(String[] lines, char separator) {
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(line);
            sb.append(separator);
        }

        return sb.toString();
    }
}
