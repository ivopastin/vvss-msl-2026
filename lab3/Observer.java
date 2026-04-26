// io/reactivex/rxjava3/subjects/PublishSubject.java
package lab3;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class Observer {
    public static void main(String[] args) {
        PublishSubject<String> chatRoom = PublishSubject.create();

        // addObserver() — sees every message
        chatRoom.subscribe(msg ->
            System.out.println("Alice: " + msg));

        // addObserver() — only sees urgent messages (operator!)
        chatRoom
            .filter(msg -> msg.contains("!"))
            .subscribe(msg ->
                System.out.println("Bob (urgent only): " + msg));

        // notifyObservers()
        chatRoom.onNext("Hello");       // -> Alice: Hello
        chatRoom.onNext("Fire alarm!");  // -> Alice: Fire alarm!
                                         // -> Bob (urgent only): Fire alarm!
    }
}