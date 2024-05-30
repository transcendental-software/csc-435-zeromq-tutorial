package csc435.app;

import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server {
    
    private String address;
    private String port;
    private Integer numWorkers;

    private ZContext context;
    private Thread proxyThread;

    public Server(String address, String port, int numWorkers) {
        this.address = address;
        this.port = port;
        this.numWorkers = numWorkers;
    }

    public void startZMQServer() {
        // ZMQ context initialized with 4 IO threads
        context = new ZContext(4);

        ZMQProxyWorker proxyWorker = new ZMQProxyWorker(context, address, port, numWorkers);
        proxyThread = new Thread(proxyWorker);
        proxyThread.start();
    }

    public void stopZMQServer() {
        for (int i = 0; i < numWorkers; i++) {
            // Create ZMQ context with 1 IO thread
            ZContext context = new ZContext(1);

            // Create ZMQ request socket and connect to server
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://127.0.0.1" + ":" + port);

            String message = "QUIT";
            byte[] buffer;
            
            socket.send(message.getBytes(ZMQ.CHARSET), 0);
            buffer = socket.recv(0);

            socket.close();
            context.close();
        }

        this.context.destroy();

        try {
            proxyThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USE: java Server <port> <number of workers");
            System.exit(1);
        }

        Server server = new Server("*", args[0], Integer.parseInt(args[1]));
        server.startZMQServer();

        Scanner sc = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print("> ");
            
            command = sc.nextLine();
            
            if (command.compareTo("quit") == 0) {
                server.stopZMQServer();
                System.out.println("Server terminated!");
                break;
            }
        }

        sc.close();
    }
}