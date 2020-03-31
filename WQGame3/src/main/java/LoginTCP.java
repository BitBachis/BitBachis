import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.nio.channels.Selector;

public class LoginTCP {

     //The nickname of the user that wants to be logged in.
    private String nickname = null;
     // The password of the user that wants to be logged in.
    private String password = null;

    //constructor
    public LoginTCP(final String nickname, final String password) throws IOException {
        this.nickname = nickname;
        this.password = password;
    }

    public void run() throws IOException {

        SocketChannel socket = SocketChannel.open();
        Selector selector = Selector.open();
        boolean connected = socket.connect(new InetSocketAddress("localhost", 6789));
        ByteBuffer toSend = ByteBuffer.wrap(nickname, password);

        if (!connected)
            socket.register(selector, SelectionKey.OP_CONNECT);
        else
            socket.register(selector, SelectionKey.OP_WRITE);
        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isConnectable()) {
                    connected = socket.finishConnect();
                    if (!connected)
                        socket.register(selector, SelectionKey.OP_CONNECT);
                    else
                        socket.register(selector, SelectionKey.OP_WRITE);
                }
                if (key.isReadable()) {
                    ByteBuffer buf = ByteBuffer.allocate(512);
                    int read = socket.read(buf);
                    if (read == -1)
                        return;
                    buf.flip();
                    System.out.print(new String(buf.array(), 0, buf.limit()));
                }
                if (key.isWritable()) {
                    socket.write(toSend);
                    if (toSend.hasRemaining())
                        socket.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                    else {
                        socket.register(selector, SelectionKey.OP_READ);
                        socket.shutdownOutput();
                    }
                }
            }
        }
    }
}
