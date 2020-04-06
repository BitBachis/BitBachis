import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WQRegistration {

    void registration(final String nick, final String pwd) throws RemoteException {

        RegistrationRMI serverObj = null;
        Remote remoteObject = null;
        // Opening the registry and locating the remote object from it.
        final Registry reg = LocateRegistry.getRegistry("localhost", 5678);
        try {
            remoteObject = reg.lookup("REGISTRATION");
            serverObj = (RegistrationRMI) remoteObject;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        // Calls the remote method and prints the result of its invocation.
        System.out.println(serverObj.registerUser(nick, pwd));
    }

}