import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client1 {
    private Socket socket;
    private DataInputStream objectInputStream;
    private DataOutputStream objectOutputStream;
    private boolean flag=true;
    Client1(){
        try {
            this.socket = new Socket("localhost",8080);
            objectInputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startUp() {
        try {
            Thread thread = new Thread(new Thread() {
                @Override
                public void run() {
                    DataInputStream objectInputStream1 = null;
                    while (flag) {
                        try {
                            objectInputStream1 = new DataInputStream(socket.getInputStream());
                            String s =null;
                            try {
                                s = objectInputStream1.readUTF();
                            }catch(SocketException e){
                                System.out.println("You have left the group chat");
                            }
                            System.out.println(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        socket.close();
                        objectInputStream1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            System.out.println("Enter your name");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.next();
            objectOutputStream.writeUTF(username);
            System.out.println("Welcome to the chat room ,Enter by to exit");
            while (flag) {
                Scanner scanner1 = new Scanner(System.in);
                String message = scanner1.nextLine();
                if (message.equalsIgnoreCase("bye")) {
                    flag = false;
                }
                objectOutputStream.writeUTF(message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(100);
                socket.close();
                objectOutputStream.close();
                objectInputStream.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
