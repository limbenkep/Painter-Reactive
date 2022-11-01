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

    private Map<Integer, Disposable> disposables;    //key: socket hash
    private Map<Integer, Disposable> messageDisposables;

    private Subject<Socket> connections;
    private Subject<String[]> messageStream;   //all messages from all clients

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
        disposables = new HashMap<>();
        messageDisposables = new HashMap<>();
        connections = PublishSubject.create();
        messageStream = ReplaySubject.<String[]>create().toSerialized();  //replay 3 last messages to new clients

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

            try{
                final Socket socket = serverSocket.accept();
                Observable.<Socket>create(emitter -> emitter.onNext(socket))
                        .observeOn(Schedulers.io())
                        .subscribe(connections);
            }catch (Throwable t){
                System.out.println("I caught you");
            }

        }

    }
    /**
     * Receives a Socket and creates an observable that listens for incoming shapes and
     * adds the shapes  to the shapes stream.
     * Then subscribe the new client to the shapes stream
     * @param socket Socket to be listened to for incoming shapes
     */
    private void listenToSocket(Socket socket) {
        String portNumber = String.valueOf(socket.getPort());
        //inject all incoming messages from this socket to the message stream
        Observable.<String[]>create(emitter -> {
                    socketToBufferedReaderLogic(socket)
                            .subscribe(br -> {
                                while(!emitter.isDisposed()) {
                                    String in = br.readLine();
                                    final String[] emission = {portNumber, in};
                                    if (in == null || socket.isClosed()) {
                                        emitter.onError(new ConnectError(socket));
                                    }
                                    else {
                                        emitter.onNext(emission);
                                    }
                                }
                            });
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> disposables.put(socket.hashCode(), d))
                .doOnError(this::handleError)
                .onErrorComplete(err -> err instanceof ConnectError)
                .doOnNext(System.out::println)
                .subscribe(messageStream::onNext
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));

        // subscribe each newly connected client to the messagestream
        messageStream
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> messageDisposables.put(socket.hashCode(), d))
                .withLatestFrom(socketToPrintWriterLogic(socket), (m, bw) -> {
                    if(!Objects.equals(m[0], portNumber)){
                        bw.write(m[1]);
                        bw.newLine();
                        bw.flush();
                    }
                    return true;
                })
                .subscribe();
    }

    Observable<BufferedWriter> socketToPrintWriterLogic(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(OutputStreamWriter::new)
                .map(BufferedWriter::new);
    }

    Observable<BufferedReader> socketToBufferedReaderLogic(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(InputStreamReader::new)
                .map(BufferedReader::new);
    }

    private void shutdown() {
        acceptConnections = false;
    }

    private void handleError(Throwable error) {
        if (error instanceof ConnectError) {
            Socket socket = ((ConnectError) error).getSocket();
            disposables.get(socket.hashCode()).dispose();
            disposables.remove(socket.hashCode());
            messageDisposables.get(socket.hashCode()).dispose();
            messageDisposables.remove(socket.hashCode());
            System.out.println("inside handle error");
        }
    }

}
