package cz.deepvision.websocket.grahpql;

public interface WebSocketCallback<T> {
    void recievedCallBack(T result, SubscriptionType type);

    void connectedCallBack();

    void disconnectedCallBack(String reason);
}
