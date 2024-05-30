package csc435.app;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Worker implements Runnable {
    private ZContext context;

    public Worker(ZContext context) {
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

            if (message.compareTo("QUIT") == 0) {
                break;
            }

            if (message.substring(0, 5).compareTo("INDEX") == 0) {
                String[] tokens = message.split("\\s+");

                System.out.println("\nindexing " + tokens[2] + " from " + tokens[1]);
                for (int i = 3; i < tokens.length; i+=2) {
                    System.out.println(tokens[i] + " " + tokens[i + 1]);
                }
                System.out.println("completed!");

                message = "OK";
                socket.send(message.getBytes(ZMQ.CHARSET), 0);

                System.out.print("> ");
                System.out.flush();

                continue;
            }

            if (message.substring(0, 6).compareTo("SEARCH") == 0) {
                String[] tokens = message.split("\\s+");

                System.out.println("\nsearching for " + tokens[0]);

                message = "DOC10 20 DOC100 30 DOC1 10";
                socket.send(message.getBytes(ZMQ.CHARSET), 0);

                System.out.print("> ");
                System.out.flush();

                continue;
            }

            message = "ERROR";
            socket.send(message.getBytes(ZMQ.CHARSET), 0);
        }

        String message = "TERMINATE";
        socket.send(message.getBytes(ZMQ.CHARSET), 0);

        socket.close();
    }
}