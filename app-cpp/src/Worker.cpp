#include "Worker.hpp"

void Worker::run() {
    // Create ZMQ reply socket for the worker
    zmq::socket_t socket(context, zmq::socket_type::rep);
    socket.connect("inproc://workers");

    while (true) {
        zmq::message_t request;
        auto res = socket.recv(request, zmq::recv_flags::none);
        std::string message = request.to_string();

        if (message.compare("quit") == 0) {
            break;
        }

        if (message.compare("addition") == 0) {
            std::string data{"2+2=4"};
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            continue;
        }

        if (message.compare("multiplication") == 0) {
            std::string data{"2x2=4"};
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            continue;
        }

        std::string data{"???"};
        socket.send(zmq::buffer(data), zmq::send_flags::none);
    }

    // Notify the main thread that the worker terminated
    server.workerTerminated();
    socket.close();
}