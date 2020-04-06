import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Set;

public class WQServer {

    private static Selector selector = null;
    private static JsonFormat.Printer jf= JsonFormat.printer();

    private final WQdb database;

    public WQServer() {
        this.database = new WQdb();
    }



    public String registerUser(final String username, final String password) {
        if (username == null) {
            System.out.println("Registration error: Invalid username.");
            return "Invalid username.";
        }
        if (password == null) {
            System.out.println("Registration error: Invalid password.");
            return "Invalid password.";
        }
        if (this.database.insertUser(username, password) == true) {
            System.out.println("Registration succeeded.");
            return "Registration succeeded.";
        } else {
            System.out.println("Registration error: Nickname already taken.");
            return "Nickname already taken.";
        }
    }

    public static void readMsg(SelectionKey key,SocketChannel client) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int nb= client.read(buffer);
        if(nb<0) {
            Socket socket = client.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddr);
            client.close();
            key.cancel();
            return;
        } else {
            System.out.println("read "+nb+" bytes");
            buffer.flip();
            int length=buffer.getInt();
            char type = buffer.getChar();
            switch(type) {
                case 1:
                    Messages.Login l = Messages.Login.parseFrom(buffer);
                    System.out.println(jf.print(l));
                    break;
            }
        }

    }

    public static void main(String[] args) throws RemoteException {
        /*
        WQServer server = new WQServer();

        // Remote method registration.
        LocateRegistry.createRegistry(5678);
        final Registry r = LocateRegistry.getRegistry(5678);
        r.rebind("REGISTRATION", (Remote) server);
        */
        try{
            System.out.println("Server listening...");
            selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress("localhost",9090));
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector,ops,null);

            while(true){
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();
                System.out.println("+");
                while(i.hasNext()){
                    SelectionKey key = i.next();
                    if(key.isAcceptable()){
                        System.out.println("Connection Accepted...");
                        SocketChannel client = socket.accept();
                        client.configureBlocking(false);
                        readMsg(key,client);
                        client.register(selector,SelectionKey.OP_READ);
                    } else if(key.isReadable()){
                        readMsg(key,(SocketChannel)(key.channel()));
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.flush();
        }
    }
}
