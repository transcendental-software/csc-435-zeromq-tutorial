#include "Worker.hpp"

void Worker::run() {
    // Create ZMQ reply socket for the worker
    zmq::socket_t socket(context, zmq::socket_type::rep);
    socket.connect("inproc://workers");

    while (true) {
        zmq::message_t request;
        auto res = socket.recv(request, zmq::recv_flags::none);
        std::string message = request.to_string();

        if (message.compare("QUIT") == 0) {
            break;
        }

        if (message.substr(0, 5).compare("INDEX") == 0) {
            std::string token;
            std::vector<std::string> tokens;
            std::stringstream message_stream(message);
            while (std::getline(message_stream, token, ' ')) {
                tokens.push_back(token);
            }

            std::cout << std::endl << "indexing " << tokens[2] << " from " << tokens[1] << std::endl;
            for (auto i = 3; i < tokens.size(); i += 2) {
                std::cout << tokens[i] << " " << tokens[i + 1] << std::endl;
            }
            std::cout << "completed!" << std::endl;

            std::string data{"OK"};
            socket.send(zmq::buffer(data), zmq::send_flags::none);

            std::cout << "> " << std::flush;
            
            continue;
        }

        if (message.substr(0, 6).compare("SEARCH") == 0) {
            std::string token;
            std::vector<std::string> tokens;
            std::stringstream message_stream(message);
            while (std::getline(message_stream, token, ' ')) {
                tokens.push_back(token);
            }

            std::cout << std::endl << "searching for " << tokens[0] << std::endl;

            std::string data{"DOC10 20 DOC100 30 DOC1 10"};
            socket.send(zmq::buffer(data), zmq::send_flags::none);

            std::cout << "> " << std::flush;
            
            continue;
        }

        std::string data{"ERROR"};
        socket.send(zmq::buffer(data), zmq::send_flags::none);
    }

    std::string data{"TERMINATE"};
    socket.send(zmq::buffer(data), zmq::send_flags::none);

    socket.close();
}