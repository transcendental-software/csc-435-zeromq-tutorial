#ifndef SERVER_H
#define SERVER_H

#include <string>

#include <zmq.hpp>
#include <thread>

class Server
{
    std::string address;
    std::string port;
    int numWorkers;
    
    zmq::context_t context;
    std::thread ZMQthread;
    
    public:
        Server(std::string address, std::string port, int numWorkers) : 
            address(address), port(port), numWorkers(numWorkers) { }
        virtual ~Server() = default;

        virtual void startZMQServer();
        virtual void stopZMQServer();

        virtual void run();
};

#endif