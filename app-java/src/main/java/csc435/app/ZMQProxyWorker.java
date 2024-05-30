package csc435.app;

import java.util.ArrayList;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZMQProxyWorker implements Runnable {
    private String address;
    private String port;
    private Integer numWorkers;

    private ZContext context;

    public ZMQProxyWorker(ZContext context, String address, String port, int numWorkers) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.numWorkers = numWorkers;
    }

    @Override
    public void run() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        
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
}
