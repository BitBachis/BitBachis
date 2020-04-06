import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;


public class WQdb {

    private ConcurrentHashMap<String, WQUser> users;

    public WQdb(){
        final File file = new File("file.json");
    }

    public boolean insertUser(String nickname,String password){
        WQUser user = new WQUser(nickname,password);
        return true;
    }


}
