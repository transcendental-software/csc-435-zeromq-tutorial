#include <iostream>
#include <vector>
#include <thread>
#include <memory>
#include <string>

#include <zmq.hpp>

#include "Worker.hpp"

class Server
{
    std::string address;
    std::string port;
    int numWorkers;
    
    zmq::context_t context;
    
    public:
        Server(std::string address, std::string port, int numWorkers) : 
            address(address), port(port), numWorkers(numWorkers) { }
        virtual ~Server() = default;

        void run()
        {
            // Keep track of the worker objects and worker threads
            std::vector<std::shared_ptr<Worker>> workers;
            std::vector<std::thread> threads;

            // ZMQ context initialized with 4 IO threads
            context = zmq::context_t(4);

            // Create ZMQ router and dealer sockets
            zmq::socket_t routerSocket = zmq::socket_t(context, zmq::socket_type::router);
            zmq::socket_t dealerSocket = zmq::socket_t(context, zmq::socket_type::dealer);

            // Bind the router socket to the server listening address and port
            // Bind the dealer socket to internal communcation channel
            routerSocket.bind("tcp://" + address + ":" + port);
            dealerSocket.bind("inproc://workers");

            // Create worker threads that connect to the dealer
            for (auto i = 0; i < numWorkers; i++) {
                std::shared_ptr<Worker> worker = std::make_shared<Worker>(std::ref(context));
                workers.push_back(worker);
                threads.push_back(std::thread(&Worker::run, worker));
            }

            // Create the ZMQ queue that forwards messages between the router and the dealer
            try {
                zmq::proxy(routerSocket, dealerSocket);
            } catch(zmq::error_t& error) { }

            // Join worker threads
            for (auto i = 0; i < numWorkers; i++) {
                threads[i].join();
            }

            routerSocket.close();
            dealerSocket.close();
            context.close();
        }

        void shutdown()
        {
            for (auto i = 0; i < numWorkers; i++) {
                // Create ZMQ context with 1 IO thread
                zmq::context_t context(1);

                // Create ZMQ request socket and connect to server
                zmq::socket_t socket(context, zmq::socket_type::req);
                socket.connect("tcp://127.0.0.1:" + port);

                std::string data{"QUIT"};
                zmq::message_t reply;

                socket.send(zmq::buffer(data), zmq::send_flags::none);
                auto res = socket.recv(reply, zmq::recv_flags::none);

                socket.close();
                context.close();
            }

            // Shutdown the ZMQ Proxy
            context.shutdown();
        }
};

int main(int argc, char** argv)
{
    if (argc != 3) {
        std::cerr << "USE: ./server <port> <number of worker threads>" << std::endl;
        return 1;
    }

    std::string port(argv[1]);
    int numWorkers(std::atoi(argv[2]));

    Server server("*", port, numWorkers);
    std::thread serverThread(&Server::run, &server);

    std::string command;

    std::cout << "ZeroMQ Server started!" << std::endl;
    while (true) {
        std::cout << "> ";
        std::getline(std::cin, command);

        if (command.compare("quit") == 0) {
            server.shutdown();
            serverThread.join();
            std::cout << "ZeroMQ Server terminated!" << std::endl;
            break;
        }
    }
    
    return 0;
}