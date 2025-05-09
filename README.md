## CSC 435 ZeroMQ Sockets Tutorial
**Jarvis College of Computing and Digital Media - DePaul University**

### Requirements

To run the C++ programs you will need to have GCC 14.x and CMake 3.28.x installed on your system. You will also need to install the ZeroMQ libraries and development files. On Ubuntu 24.04 LTS you can install GCC and set it as default compiler using the following commands:
```
sudo apt install build-essential cmake g++-14 gcc-14 cmake
sudo update-alternatives --remove-all gcc
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-13 130
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-14 140
sudo update-alternatives --remove-all g++
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-13 130
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-14 140
sudo apt install pkg-config libzmq3-dev libzmq5 libczmq-dev libczmq4 cppzmq-dev
```

To run the Java programs you will need to have Java 21.x and Maven 3.8.x installed on your systems. You will also need to install the JeroMQ (Java ZeroMQ) library and development jar. On Ubuntu 24.04 LTS you can install Java, Maven and JeroMQ using the following commands:

```
sudo apt install openjdk-21-jdk maven libjeromq-java

```

### C++ solution
#### How to build/compile

To build the C++ solution use the following commands:
```
cd app-cpp
mkdir build
cmake -S . -B build
cmake --build build --config Release
```

#### How to run application

To run the C++ server (after you build the project) use the following command:
```
./build/server <port> <num worker threads>
> [quit]
```

To run the C++ client (after you build the project) use the following command:
```
./build/client <client ID> <IP address> <port>
```

#### Example

Server
```
./build/server 12345 4
ZeroMQ Server started!
> 
indexing DOC11 from client 1
tiger 100
cat 10
dog 20
completed!
> 
searching for cat
> 
indexing DOC11 from client 2
tiger 100
cat 10
dog 20
completed!
> 
searching for cat
> quit
ZeroMQ Server terminated!
```

Client 1
```
./build/client 1 127.0.0.1 12345
Indexing OK
Searching for cat
DOC10 20
DOC100 30
DOC1 10
```

Client 2
```
./build/client 2 127.0.0.1 12345
Indexing OK
Searching for cat
DOC10 20
DOC100 30
DOC1 10
```

### Java solution
#### How to build/compile

To build the Java solution use the following commands:
```
cd app-java
mvn compile
mvn package
```

#### How to run application

To run the Java Server (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.Server <port> <num worker threads>
> [quit]
```

To run the Java Client (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.Client <client ID> <IP address> <port>
```

#### Example

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.Server 12345 4
ZeroMQ Server started!
> 
indexing DOC11 from client 1
cat 10
tiger 100
dog 20
completed!
> 
searching for cat
> 
indexing DOC11 from client 2
cat 10
tiger 100
dog 20
completed!
> 
searching for cat
> quit
ZeroMQ Server terminated!
```

Client 1
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.Client 1 127.0.0.1 12345
Indexing OK
Searching for cat
DOC10 20
DOC100 30
DOC1 10
```

Client 2
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.Client 2 127.0.0.1 12345
Indexing OK
Searching for cat
DOC10 20
DOC100 30
DOC1 10
```
