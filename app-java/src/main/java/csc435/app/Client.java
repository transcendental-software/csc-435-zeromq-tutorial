package csc435.app;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    private Integer clientID;
    private String address;
    private Integer port;

    public Client(Integer clientID, String address, int port) {
        this.clientID = clientID;
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
        
        message = "INDEX Client" + clientID.toString() + " DOC11 tiger 100 cat 10 dog 20";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);
        buffer = socket.recv(0);
        message = new String(buffer, ZMQ.CHARSET);
        System.out.println("Indexing " + message);

        message = "SEARCH cat";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);
        buffer = socket.recv(0);
        message = new String(buffer, ZMQ.CHARSET);

        System.out.println("Searching for cat");
        String[] tokens = message.split("\\s+");
        for (int i = 0; i < tokens.length; i += 2) {
            System.out.println(tokens[i] + " " + tokens[i + 1]);
        }

        socket.close();
        context.close();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("USE: java Client <client ID> <IP address> <port>");
            System.exit(1);
        }

        Client client = new Client(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
        client.run();
    }
}