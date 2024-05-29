#ifndef WORKER_H
#define WORKER_H

#include <iostream>

#include <zmq.hpp>

#include "Server.hpp"

class Worker
{
    Server& server;
    zmq::context_t& context;

    public:
        Worker(Server& server, zmq::context_t& context) : server(server), context(context) { }
        virtual ~Worker() = default;

        virtual void run();
};

#endif