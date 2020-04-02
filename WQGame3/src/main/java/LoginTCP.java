import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;

import static picocli.CommandLine.*;

public class LoginTCP implements Runnable{

    private String nickname;
    private String password;

    //constructor
    public LoginTCP(final String nickname, final String password) throws IOException {
        this.nickname = nickname;
        this.password = password;
    }

        public void run()  {

            System.out.println("Hello "+nickname+" "+password);
            try {
                String[] data = {nickname,password};
                InetSocketAddress address = new InetSocketAddress("localhost",9090);
                SocketChannel client = SocketChannel.open(address);

                    for(String s : data){
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put(s.getBytes());
                        buffer.flip();
                        int bytesWritten = client.write(buffer);
                        System.out.println(String.format("Sending Data: %s\n: %d",s, bytesWritten));
                    }
                    client.close();
                    System.out.println("Client connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
