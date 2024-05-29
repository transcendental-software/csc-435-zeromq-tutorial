package csc435.app;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Worker implements Runnable {
    private Server server;
    private ZContext context;

    public Worker(Server server, ZContext context) {
        this.server = server;
        this.context = context;
    }

    @Override
    public void run() {
        // Create ZMQ reply socket for the worker
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.connect("inproc://workers");
        
        while(true) {
            byte[] buffer = socket.recv(0);
            String message = new String(buffer, ZMQ.CHARSET);

            if (message.compareTo("quit") == 0) {
                break;
            }

            if (message.compareTo("addition") == 0) {
                message = "2+2=4";
                socket.send(message.getBytes(ZMQ.CHARSET), 0);
                continue;
            }

            if (message.compareTo("multiplication") == 0) {
                message = "2x2=4";
                socket.send(message.getBytes(ZMQ.CHARSET), 0);
                continue;
            }

            message = "???";
            socket.send(message.getBytes(ZMQ.CHARSET), 0);
        }

        // Notify the main thread that the worker terminated
        server.workerTerminate();
        socket.close();
    }
}