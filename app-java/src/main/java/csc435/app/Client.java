package csc435.app;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    private String address;
    private Integer port;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {
        // Create ZMQ context with 1 IO thread
        ZContext context = new ZContext(1);

        // Create ZMQ request socket and connect to server
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://" + address + ":" + port);

        String message;
        byte[] buffer;
        
        message = "addition";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);
        buffer = socket.recv(0);
        System.out.println(new String(buffer, ZMQ.CHARSET));

        message = "multiplication";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);
        buffer = socket.recv(0);
        System.out.println(new String(buffer, ZMQ.CHARSET));

        message = "quit";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);

        socket.close();
        context.close();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USE: java Client <IP address> <port>");
            System.exit(1);
        }

        Client client = new Client(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}