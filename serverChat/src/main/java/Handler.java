import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Handler implements Runnable{
    private Socket socket;
    List<Handler> list;
    private DataInputStream objectInputStream;
    private DataOutputStream objectOutputStream;
    private SimpleDateFormat date = new SimpleDateFormat( "HH:mm:ss");
    private boolean flag=true;
    private String username;
    Handler(Socket socket, List<Handler> list){
        this.socket = socket;
        this.list = list;
        this.list.add(this);
        try {
            objectInputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new DataOutputStream(socket.getOutputStream());
            String name = objectInputStream.readUTF();
            username = name;
            System.out.println("[" + date.format(new Date()) + "] " + username + ": Join this chat");
            send("[" + date.format(new Date()) + "] "+username+": Join this chat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(String s) {
        list.forEach((handler)-> {
            try {
                if(handler.socket!=socket) {
                    handler.objectOutputStream.writeUTF(s);
                    handler.objectOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void receive() throws SQLException {
        Connection root = DriverManager.getConnection("jdbc:mysql://localhost:3306/messagedb", "root", "");
        PreparedStatement preparedStatement=null;
        while (flag){
            try {
                String answer = (String)objectInputStream.readUTF();
                String dateM = date.format(new Date());
                preparedStatement = root.prepareStatement("insert into `multi_chat`(date,name,message)" +
                        "values (?,?,?)");
                preparedStatement.setString(1,dateM);
                preparedStatement.setString(2,username);
                preparedStatement.setString(3,answer);
                preparedStatement.executeUpdate();
                if(!answer.equalsIgnoreCase("bye")){
                    System.out.println("[" + dateM + "] "+username + ": " + answer);
                    send("[" + dateM + "]"+username +": " + answer);
                }
                else{
                    System.out.println("[" + dateM + "] "+username + ": Leave this chat");
                    send("[" + dateM + "] "+username + ": Leave this chat");
                    objectOutputStream.flush();
                    list.remove(this);
                    flag=false;

                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        preparedStatement.close();
    }
    @Override
    public void run() {
        while(flag){
            try {
                receive();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

}
