import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Handler> clients  = new ArrayList<>();

    public  void startUp() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while(true){
                Socket accept = serverSocket.accept();
                System.out.println("accepted");
                Handler handler = new Handler(accept,clients);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
