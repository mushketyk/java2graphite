package java2graphite.core;

import javax.net.SocketFactory;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author <a href="mailto:ivan.mushketyk@gmail.com">Ivan Mushketyk</a>
 */
public class GraphitePlaintextClient implements GraphiteClient {

    private InetSocketAddress serverAddress;

    private SocketFactory socketFactory;

    private Clock clock;
    private Socket socket;
    private Writer writer;

    public GraphitePlaintextClient(InetSocketAddress serverAddress, SocketFactory socketFactory) {

    }

    GraphitePlaintextClient(InetSocketAddress serverAddress, SocketFactory socketFactory, Clock clock) {
        this.serverAddress = serverAddress;
        this.socketFactory = socketFactory;
        this.clock = clock;
    }

    @Override
    public void connect() throws IOException {
        socket = socketFactory.createSocket(serverAddress.getHostName(), serverAddress.getPort());
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void send(String path, Number value) throws IOException {
        long currentTime = clock.currentTime();

        writer.write(path);
        writer.write(' ');
        writer.write(value.toString());
        writer.write(' ');
        writer.write(Long.toString(currentTime));
        writer.write('\n');
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
        socket.close();
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    Clock getClock() {
        return clock;
    }
}
