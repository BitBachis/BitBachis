import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationRMI extends Remote {

    public String registerUser(String username, String password) throws RemoteException;

}
