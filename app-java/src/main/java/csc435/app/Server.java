package csc435.app;

import java.util.ArrayList;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Server {
    
    private String address;
    private String port;
    private Integer numWorkers;
    private Integer numTerminatedWorkers;

    private ZContext context;
    private ZMQ.Socket routerSocket;
    private ZMQ.Socket dealerSocket;

    public Server(String address, String port, int numWorkers) {
        this.address = address;
        this.port = port;
        this.numWorkers = numWorkers;
        numTerminatedWorkers = 0;
    }

    public void run() {
        ArrayList<Thread> threads = new ArrayList<Thread>();

        // ZMQ context initialized with 4 IO threads
        context = new ZContext(4);
        
        // Create ZMQ router and dealer sockets
        routerSocket = context.createSocket(SocketType.ROUTER);
        dealerSocket = context.createSocket(SocketType.DEALER);

        // Bind the router socket to the server listening address and port
        // Bind the dealer socket to worker internal communcation channel
        routerSocket.bind("tcp://" + address + ":" + port);
        dealerSocket.bind("inproc://workers");

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(this, context);
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

    public synchronized void workerTerminate() {
        numTerminatedWorkers++;

        // Stop after two workers terminated
        if (numTerminatedWorkers >= 2) {
            context.destroy();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("USE: java Server <IP address> <port> <number of workers");
            System.exit(1);
        }

        Server server = new Server(args[0], args[1], Integer.parseInt(args[2]));
        server.run();
    }
}