#include <iostream>
#include <string>

#include <zmq.hpp>

class Client
{
    int clientID;
    std::string address;
    std::string port;

    public:
        Client(int clientID, std::string address, std::string port) :
            clientID(clientID), address(address), port(port) { }
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

            data = "INDEX Client" + std::to_string(clientID) + " DOC11 tiger 100 cat 10 dog 20";
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            auto res = socket.recv(reply, zmq::recv_flags::none);
            std::cout << "Indexing " << reply.to_string() << std::endl;

            data = "SEARCH cat";
            socket.send(zmq::buffer(data), zmq::send_flags::none);
            res = socket.recv(reply, zmq::recv_flags::none);
            
            std::string token;
            std::vector<std::string> tokens;
            std::stringstream message_stream(reply.to_string());
            std::cout << "Searching for cat" << std::endl;
            while (std::getline(message_stream, token, ' ')) {
                tokens.push_back(token);
            }
            for (auto i = 0; i < tokens.size(); i += 2) {
                std::cout << tokens[i] << " " << tokens[i + 1] << std::endl;
            }

            socket.close();
            context.close();
        }
};

int main(int argc, char** argv)
{
    if (argc != 4) {
        std::cerr << "USE: ./client <client ID> <IP address> <port>" << std::endl;
        return 1;
    }

    std::string clientID(argv[1]);
    std::string address(argv[2]);
    std::string port(argv[3]);

    Client client(std::stoi(clientID), address, port);
    client.run();

    return 0;
}