package java2graphite.core;

import java.io.IOException;

/**
 * @author <a href="mailto:ivan.mushketyk@gmail.com">Ivan Mushketyk</a>
 */
public interface GraphiteClient {
    void connect() throws IOException;

    void send(String path, Number value) throws IOException;

    void close() throws IOException;
}
