import java.util.ArrayList;

public class WQUser implements Comparable<WQUser>{

    private final String nickname;
    private final String password;
    //private Integer score;
    //private final ArrayList<String> friends;


    public WQUser(String nickname, String password/*, Integer score, ArrayList<String> friends*/) {
        this.nickname = nickname;
        this.password = password;
        //this.score = score;
        //this.friends = friends;
    }


    @Override
    public int compareTo(WQUser o) {
        return 0;
    }
}
