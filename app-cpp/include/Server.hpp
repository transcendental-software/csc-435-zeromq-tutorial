#ifndef SERVER_H
#define SERVER_H

#include <string>

#include <zmq.hpp>
#include <mutex>

class Server
{
    std::string address;
    std::string port;
    int numWorkers;
    int numTerminatedWorkers;
    
    zmq::context_t context;
    std::mutex mutex;
    
    public:
        Server(std::string address, std::string port, int numWorkers) : 
            address(address), port(port), numWorkers(numWorkers), numTerminatedWorkers(0), context(4) { }
        virtual ~Server() = default;

        virtual void run();

        virtual void workerTerminated();
};

#endif