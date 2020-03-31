import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    // channel selector
    private Selector selector;
    // SOCKET for tcp connection (login)
    private ServerSocketChannel socket;
    // port for tcp connection
    private final int mainPort = 6789;
    //data structure for onlineUser
    //private final ConcurrentHashMap<Integer, String> onlineUsers;


    //constructor
    public Server() throws RemoteException {
        try {
            socket = ServerSocketChannel.open();
            socket.bind(new InetSocketAddress(mainPort));
            socket.configureBlocking(false);
            selector = Selector.open();
            socket.register(selector,SelectionKey.OP_ACCEPT,SelectionKey.OP_READ);
        } catch (final IOException IOE) {
            IOE.printStackTrace();
        }
    }


    public void run() {
        while(true) {
            try {
                selector.select();
                for(SelectionKey key : selector.selectedKeys()) {
                    if(key.isAcceptable()){ //connection accepted by ServerSocketChannel
                        SocketChannel client = socket.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                    }
                    if(key.isReadable()){ // channel ready for reading
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer inBuf = ByteBuffer.allocate(1024);
                        while (client.read(inBuf) > 0) {
                            System.out.printf("[%s]:\t%s\n", new String(inBuf.array()));
                        }
                    }
                    if(key.isWritable()){  //channel ready for writing
                        SocketChannel client = (SocketChannel) key.channel();
                        String response = "hi - from non-blocking server";
                        byte[] bs = response.getBytes(StandardCharsets.UTF_8);
                        ByteBuffer buffer = ByteBuffer.wrap(bs);
                        client.write(buffer);

                        // switch to read, and disable write,
                        client.register(selector, SelectionKey.OP_READ);
                    }
                }
                selector.selectedKeys().clear();
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) throws RemoteException {
        Server s = new Server();
        s.run();
    }

}

