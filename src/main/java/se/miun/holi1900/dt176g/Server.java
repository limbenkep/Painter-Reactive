package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.ReplaySubject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Server {


    public static void processConnection() throws Exception {
        ReplaySubject<String> stringReplaySubject = ReplaySubject.create();
        ServerSocket ssock = new ServerSocket(11111);
        List<BufferedWriter> writers = new ArrayList<>();
        List<ObjectOutputStream> outputStreams = new ArrayList<>();
        List<Socket> sockets = new ArrayList<>();
        System.out.println("creating stream of clients");
        Stream<Socket> clients = Stream.generate(() -> {
            try {
                System.out.println("Accepting a connection on " + thread());
                return ssock.accept();
            } catch (Throwable t) {
                return null;
            }
        });
        Observable<String> subject = createSubject();
        //Observable<String> observable =
                Observable.<Socket>create(e -> {
            //while (!ssock.isClosed())
                    while (true) {
                        System.out.println("Accepting a connection on " + thread());
                        e.onNext(ssock.accept());
                    }
                })
                .observeOn(Schedulers.io())
                //.map(Socket::getPort)
                .doOnNext(s->{
                    sockets.add(s);
                    writers.add(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));

                })
                //.subscribeOn(Schedulers.single())
                .map(Socket::getInputStream)
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .map(BufferedReader::lines)
                .flatMap(stream -> Observable.fromIterable(() -> stream.iterator()).subscribeOn(Schedulers.computation()))
                        .subscribe(stringReplaySubject::onNext);
        //Disposable d = Observable.fromIterable(clients::iterator).subscribeOn(Schedulers.computation()).subscribe(s->createConsumer(observable, s));
                //.subscribe();
                //.subscribe(stream -> stream.forEach(System.out::println));
                //.subscribeOn(Schedulers.computation())
                //.subscribe(System.out::println);
                //.blockingSubscribe(s -> System.out.println(thread() + ": " + s));
                //.observeOn(Schedulers.computation())
                /*.subscribe(s->{
                    System.out.println(thread() + ": " + s);
                    /*for(BufferedWriter out: writers){
                        out.write(s);
                        out.newLine();
                        out.flush();
                    }*/
                    /*for(Socket socket: sockets){
                        if(socket.isConnected()){
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            out.write(s);
                            out.newLine();
                            out.flush();
                        }
                    }
                });*/




        /*
        ServerSocket ssock = new ServerSocket(11111);
        List<ObjectOutputStream> outputStreams = new ArrayList<>();
        List<Socket> sockets = new ArrayList<>();
        System.out.println("creating stream of clients");
        Observable.<Socket>create(e -> {
            //while (!ssock.isClosed())
                    while (true) {
                        System.out.println("Accepting a connection on " + thread());
                        e.onNext(ssock.accept());
                    }
                })
                //.subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.just(s)
                        .subscribeOn(Schedulers.io())
                        .map(socket->{
                            outputStreams.add(new ObjectOutputStream(socket.getOutputStream()));
                            return socket.getInputStream();
                        })
                                .map(ObjectInputStream::new)
                                .flatMap(o->Observable.create(emitter -> {
                                    while (true){
                                        emitter.onNext(o.readObject());
                                    }
                                }).subscribeOn(Schedulers.io()))
                                //.map(ObjectInputStream::readObject)
                                //.flatMap(stream->Observable.fromIterable(() -> stream.iterator()).subscribeOn(Schedulers.io()))
                )
                .observeOn(Schedulers.computation())
                .subscribe(s->{
                    System.out.println(thread() + ": " + s.toString());
                    for(ObjectOutputStream out: outputStreams){
                        out.writeObject(s);
                        out.flush();
                    }
                });
         */
    }

    public static Consumer<String> createConsumer(Observable<String> shapesObservable, Socket socket){
        return event ->{
           final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            shapesObservable.subscribe(s->{
                out.write(s);
                out.newLine();
                out.flush();
            });
        };
    }

    public static ReplaySubject<String> createSubject(){
        return ReplaySubject.create();
    }

    public static String thread() {
        Thread current = Thread.currentThread();
        return "(Thread: " + current + " (id = " + current.getId() + "))";
    }

    public static void main(String[] args) throws Exception {
        processConnection();

        // Sleeping is only necessary when a subscribeOn is added outside of the flatMap so that
        // the main thread is not used and therefore rushes to the end.
        System.out.println(" !! SLEEPING...");
        Thread.sleep(Long.MAX_VALUE);

        System.out.println("end of main");
    }


}
