package csc435.app;

import java.util.ArrayList;
import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server implements Runnable {
    
    private String address;
    private String port;
    private Integer numWorkers;

    private ZContext context;

    public Server(String address, String port, int numWorkers) {
        this.address = address;
        this.port = port;
        this.numWorkers = numWorkers;
    }

    @Override
    public void run() {
        ArrayList<Thread> threads = new ArrayList<Thread>();

        // ZMQ context initialized with 4 IO threads
        context = new ZContext(numWorkers);
        
        // Create ZMQ router and dealer sockets
        ZMQ.Socket routerSocket = context.createSocket(SocketType.ROUTER);
        ZMQ.Socket dealerSocket = context.createSocket(SocketType.DEALER);

        // Bind the router socket to the server listening address and port
        // Bind the dealer socket to worker internal communcation channel
        routerSocket.bind("tcp://" + address + ":" + port);
        dealerSocket.bind("inproc://workers");

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(context);
            Thread thread = new Thread(worker);
            thread.start();
            threads.add(thread);
        }

        // Create the ZMQ queue that forwards messages between the router and the dealer
        ZMQ.proxy(routerSocket, dealerSocket, null);

        try {
            for (int i = 0; i < numWorkers; i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        routerSocket.close();
        dealerSocket.close();
        context.close();
    }

    public void shutdown() {
        for (int i = 0; i < numWorkers; i++) {
            // Create ZMQ context with 1 IO thread
            ZContext context = new ZContext(1);

            // Create ZMQ request socket and connect to server
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://127.0.0.1" + ":" + port);

            String message = "QUIT";
            
            socket.send(message.getBytes(ZMQ.CHARSET), 0);
            socket.recv(0);

            socket.close();
            context.close();
        }

        context.destroy();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USE: java Server <port> <number of workers");
            System.exit(1);
        }

        Server server = new Server("*", args[0], Integer.parseInt(args[1]));
        Thread serverThread = new Thread(server);
        serverThread.start();

        Scanner sc = new Scanner(System.in);
        String command;

        System.out.println("ZeroMQ Server started!");
        while (true) {
            System.out.print("> ");
            
            command = sc.nextLine();
            
            if (command.compareTo("quit") == 0) {
                server.shutdown();
                try {
                    serverThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ZeroMQ Server terminated!");
                break;
            }
        }

        sc.close();
    }
}