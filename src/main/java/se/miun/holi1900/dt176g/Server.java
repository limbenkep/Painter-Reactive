package se.miun.holi1900.dt176g;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void processConnection() throws Exception {
        ServerSocket ssock = new ServerSocket(11111);
        List<BufferedWriter> outs = new ArrayList<>();
        List<Socket> sockets = new ArrayList<>();
        System.out.println("creating stream of clients");
        Observable.<Socket>create(e -> {
            //while (true)
                    while (!ssock.isClosed()) {
                        System.out.println("Accepting a connection on " + thread());
                        e.onNext(ssock.accept());
                    }
                    e.onComplete();
                }).subscribeOn(Schedulers.io()).flatMap(s -> Observable.just(s)
                        .subscribeOn(Schedulers.io())
                        .map(socket->{
                            outs.add(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                            //sockets.add(socket);
                            return socket.getInputStream();
                        })
                        .map(InputStreamReader::new)
                        .map(BufferedReader::new)
                        .map(BufferedReader::lines)
                        .flatMap(stream -> Observable.fromIterable(() -> stream.iterator()).subscribeOn(Schedulers.io()))
                )
                .subscribe(s->{
                    System.out.println(thread() + ": " + s);
                    for(BufferedWriter out: outs){
                        out.write(s);
                        out.newLine();
                        out.flush();
                    }
                });
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
