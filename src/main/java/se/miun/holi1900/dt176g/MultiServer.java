package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultiServer {
    public class ConnectError extends Throwable {

        private static final long serialVersionUID = 1L;
        private final Socket socket;

        public ConnectError(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    // socket hash: disposable for streams of incoming shapes from the different sockets
    private Map<Integer, Disposable> incomingDisposables;    //key: socket hash
    // socket hash: disposable for streams sending shapes through the different sockets
    private Map<Integer, Disposable> broadcastDisposables;
    private Subject<Socket> connections; //all sockets of connected clients
    private Subject<String[]> shapeStream;   //all shapes from all clients
    private ServerSocket serverSocket;

    private boolean acceptConnections = true;


    public static void main(String[] args) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            MultiServer server = new MultiServer();
            Disposable disposable = Observable.just(server)
                    .subscribeOn(Schedulers.single())
                    .doOnNext(MultiServer::run)
                    .doOnDispose(server::shutdown)
                    .doOnSubscribe(d -> System.out.println("Server is running...press <enter> to stop"))
                    .subscribe();

            br.readLine();

            disposable.dispose();
            Schedulers.shutdown();

            System.out.println("-- Shutting down --");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        incomingDisposables = new HashMap<>();
        broadcastDisposables = new HashMap<>();
        connections = PublishSubject.create();
        shapeStream = ReplaySubject.<String[]>create().toSerialized();

        // listen for requests on separate thread
        Completable.create(emitter -> listenForIncomingConnectionRequests())
                .subscribeOn(Schedulers.single())
                .subscribe();

        connections
                .doOnNext(s -> System.out.println("tcp connection accepted..."))
                .subscribe(this::listenToSocket);
    }

    /**
     * Listens for connection and adds each connection to the connections stream
     * @throws IOException throw exception die to connection error
     */
    private void listenForIncomingConnectionRequests() throws IOException {
        serverSocket = new ServerSocket(11111);
        while (acceptConnections) {
            final Socket socket = serverSocket.accept();
            Observable.<Socket>create(emitter -> emitter.onNext(socket))
                    .observeOn(Schedulers.io())
                    .subscribe(connections);
        }
    }
    /**
     * Receives a Socket and creates an observable that listens for incoming shapes
     * from that socket and adds the shapes  to the shapes stream.
     * If error occurs when reading due to lost connection,
     * the socket is closes and resources released.
     * The new client is subscribed to the shapes stream
     * @param socket Socket to be listened to for incoming shapes
     */
    private void listenToSocket(Socket socket) {
        final String portNumber = String.valueOf(socket.getPort());
        //inject all incoming shapes from this socket to the shape stream
        Observable.<String[]>create(emitter -> {
            createIncomingShapesObservable(socket)
                            .subscribe(br->{
                                        final String[] emission = {portNumber, br};
                                        emitter.onNext(emission);
                                    },
                                    e->emitter.onError(new ConnectError(socket)));
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> incomingDisposables.put(socket.hashCode(), d))
                .doOnError(this::handleError)
                .onErrorComplete(err -> err instanceof ConnectError)
                .doOnNext(System.out::println)
                .subscribe(shapeStream::onNext
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));

        // subscribe each newly connected client to the shapeStream
        shapeStream
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> broadcastDisposables.put(socket.hashCode(), d))
                .withLatestFrom(getBufferedWriterObservable(socket), (m, bw) -> {
                    if(!Objects.equals(m[0], portNumber)){
                        bw.write(m[1]);
                        bw.newLine();
                        bw.flush();
                    }
                    return true;
                })
                .subscribe();
    }

    /**
     * Recieves a socket and return an Observable that emits the BufferWriter for the socket's outputStream
     * @param socket socket
     * @return Observable of type BufferedWriter
     */
    Observable<BufferedWriter> getBufferedWriterObservable(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(OutputStreamWriter::new)
                .map(BufferedWriter::new);
    }


    /**
     * receives a socket and returns an observable that listens for incoming
     * shapes and emits a stream of shapes String
     * @param socket passed socket to be listened for incoming shapes
     * @return Observable of type String containing shapes data
     */
    Observable<String> createIncomingShapesObservable(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .map(BufferedReader::lines)
                .flatMap(stream -> Observable.fromIterable(stream::iterator).subscribeOn(Schedulers.computation()));
    }

    /**
     * stop accepting connection through ServerSocket
     */
    private void shutdown() {
        acceptConnections = false;
    }

    /**
     * If error is an instance of ConnectError, the socket that caused the error
     * is extracted from the ConnectError object
     * and all resources connected to this socket are released
     * @param error
     */
    private void handleError(Throwable error) {
        if (error instanceof ConnectError) {
            Socket socket = ((ConnectError) error).getSocket();
            incomingDisposables.get(socket.hashCode()).dispose();
            incomingDisposables.remove(socket.hashCode());
            broadcastDisposables.get(socket.hashCode()).dispose();
            broadcastDisposables.remove(socket.hashCode());
        }
    }

}
