

import com.google.protobuf.Message;
import jdk.nashorn.internal.runtime.JSONFunctions;
import picocli.CommandLine;
import com.google.protobuf.util.JsonFormat;
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
@Command(name = "login", mixinStandardHelpOptions = true,
        description = "Log in to WQ game",version = "1.0")
public class LoginTCP implements Runnable{
    JsonFormat.Printer jf= JsonFormat.printer();
    @ParentCommand
    Commands.CliCommands parent;

    @CommandLine.Parameters(index = "0")    private String nickname;
    @CommandLine.Parameters(index = "1")    private String password;



        public void run()  {

            System.out.println("Hello "+nickname+" "+password);
            try {
                Messages.Login login=Messages.Login.newBuilder().setNickname(nickname).setPassword(password).build();
                byte[] content=login.toByteArray();
                ByteBuffer bb=ByteBuffer.allocateDirect(content.length+6);
                bb.putInt(content.length);
                bb.putChar((char)1); // message type
                bb.put(content);
                bb.flip();
                InetSocketAddress address = new InetSocketAddress("localhost",9090);
                SocketChannel client = SocketChannel.open(address);
                int bytesWritten=client.write(bb);
                System.out.println(String.format("Sent %d bytes: %s", bytesWritten,jf.print(login)));
                client.close();
                System.out.println("Client connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
