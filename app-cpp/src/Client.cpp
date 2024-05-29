#include <iostream>

#include <zmq.hpp>

class Client
{
    std::string address;
    std::string port;

    public:
        Client(std::string address, std::string port) : address(address), port(port) { }
        virtual ~Client() = default;

        virtual void run()
        {
            // Create ZMQ context with 1 IO thread
            zmq::context_t context(1);

            // Create ZMQ request socket and connect to server
            zmq::socket_t socket(context, zmq::socket_type::req);
            socket.connect("tcp://" + address + ":" + port);

            std::string data;
            zmq::message_t reply;

            data = "addition";
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            auto res = socket.recv(reply, zmq::recv_flags::none);
            std::cout << reply.to_string() << std::endl;

            data = "multiplication";
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            res = socket.recv(reply, zmq::recv_flags::none);
            std::cout << reply.to_string() << std::endl;

            data = "quit";
            socket.send(zmq::buffer(data), zmq::send_flags::none);

            socket.close();
            context.close();
        }
};

int main(int argc, char** argv)
{
    if (argc != 3) {
        std::cerr << "USE: ./client <IP address> <port>" << std::endl;
        return 1;
    }

    std::string address(argv[1]);
    std::string port(argv[2]);

    Client client(address, port);
    client.run();

    return 0;
}