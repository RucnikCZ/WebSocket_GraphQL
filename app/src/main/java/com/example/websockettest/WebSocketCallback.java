package com.example.websockettest;

public interface WebSocketCallback<T> {
    void recievedCallBack(T result, SubscriptionType type);

    void connectedCallBack();

    void disconnectedCallBack();
}
