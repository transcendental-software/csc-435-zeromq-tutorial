#ifndef WORKER_H
#define WORKER_H

#include <iostream>

#include <zmq.hpp>

#include "Server.hpp"

class Worker
{
    zmq::context_t& context;

    public:
        Worker(zmq::context_t& context) : context(context) { }
        virtual ~Worker() = default;

        virtual void run();
};

#endif