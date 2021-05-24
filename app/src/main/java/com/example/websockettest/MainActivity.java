package com.example.websockettest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.websockettest.graphql.UpdatedKitchenRecipesSubscription;
import com.example.websockettest.graphql.UpdatedOrderCountSubscription;
import com.example.websockettest.graphql.type.OrderRecipeStateEnum;
import com.example.websockettest.graphql.type.OrderStateCategoryEnum;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private String[] permissionsList = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPerms();
        //DEV
//        RetriveLoginData loginUserTask = new RetriveLoginData("pokladni2@deepvision.cz", "pokladni2123");
        //PROD
        RetriveLoginData loginUserTask = new RetriveLoginData("rucnik", "123456789");
        loginUserTask.execute();

    }

    private void requestPerms() {
        if (checkPermissions() != 0) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList, 42);
        } else {
            Log.i("MAIN", "All permissions granted! yup");
        }
    }

    private int checkPermissions() {
        int perms = 0;
        for (String perm : permissionsList) {
            perms += ActivityCompat.checkSelfPermission(getApplicationContext(), perm);
        }
        return perms;
    }

    static class RetriveLoginData extends AsyncTask<Void, Void, Void> {
        private String login;
        private String pass;
        private boolean loggedIn = false;

        RetriveLoginData(String email, String pwd) {
            super();
            this.login = email;
            this.pass = pwd;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://c2e-p1.deep-vision.cloud/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            SpeedloService speedloService = retrofit.create(SpeedloService.class);

            final CompositeDisposable compoiteDisposable = new CompositeDisposable();
            Single<SpeedloUserToken> userToken = speedloService.getUserToken(login, pass);
            userToken.subscribe(new SingleObserver<SpeedloUserToken>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compoiteDisposable.add(d);
                }

                @Override
                public void onSuccess(SpeedloUserToken speedloUserToken) {
                    if (speedloUserToken.getError() != null) {
                        return;
                    }
                    String serverIP = "192.168.16";
                    try {
                        Socket socket = new Socket(serverIP, Integer.parseInt("8080"));

                        ServerSocket serverSocket =new ServerSocket(8080);
                        socket = serverSocket.accept();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JsonObject varibalesOrder = createOrderVariables();
                    JsonObject variables2 = createKitchenVariables();


                    JsonObject[] variablesArray = new JsonObject[2];
                    variablesArray[0] = variables2;
                    variablesArray[1] = varibalesOrder;


                    HashMap<SubscriptionType, Class<?>> stringClassHashMap = new HashMap<>();
                    stringClassHashMap.put(SubscriptionType.ORDER_SUB, UpdatedOrderCountSubscription.class);
                    stringClassHashMap.put(SubscriptionType.KITCHEN_SUB,UpdatedKitchenRecipesSubscription.class);


                    String wssUrl = "wss://c2e-p1.deep-vision.cloud/cable?token=";
                    WebSocketGraphQL webSocketGraphQL = new WebSocketGraphQL(speedloUserToken.getToken(), wssUrl, variablesArray, callback, stringClassHashMap, "POST_X",true, true);
                    webSocketGraphQL.setupWebSocket();
                }

                @Override
                public void onError(Throwable e) {
                    JsonObject variables = createOrderVariables();
                    JsonObject variables2 = createKitchenVariables();


                    JsonObject[] variablesArray = new JsonObject[2];
                    variablesArray[0] = variables;
                    variablesArray[1] = variables2;

                    HashMap<SubscriptionType, Class<?>> stringClassHashMap = new HashMap<>();

                    stringClassHashMap.put(SubscriptionType.ORDER_SUB, UpdatedOrderCountSubscription.class);
                    stringClassHashMap.put(SubscriptionType.KITCHEN_SUB,UpdatedKitchenRecipesSubscription.class);

                    String wssUrl = "wss://c2e-p1.deep-vision.cloud/cable?token=";
                    WebSocketGraphQL webSocketGraphQL = new WebSocketGraphQL("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxNTA1LCJhdXRoZW50aWNhdGlvbl90b2tlbiI6ImdoMzhEc1hmbHVjelV6TmJvNjBWQ1F0dCIsImlhdCI6MjEyNzkzOTA2LCJqdGkiOiJlOWM0OGQ3MjBlNDg4MzYxN2E3MjE5NTgxZmI5N2I5YiJ9.pDiPulEKtjWhiLIoGu1h40SyAG7LjxjyW-BjpyGSfWE", wssUrl, variablesArray, callback, stringClassHashMap,"POST_X", true, true);
                    webSocketGraphQL.setupWebSocket();
                }
            });
            return null;
        }

        @NotNull
        private JsonObject createOrderVariables() {
            JsonObject varibalesOrder = new JsonObject();
            JsonArray jArray = new JsonArray();

            //TEST POBOČKA 476
            //POD hrádkem 1 DEV
            JsonPrimitive element = new JsonPrimitive(476);
            jArray.add(element);

            varibalesOrder.add("branches", jArray);

            JsonArray catArr = new JsonArray();
            JsonPrimitive state = new JsonPrimitive(String.valueOf(OrderStateCategoryEnum.safeValueOf("PREPARING")));
            catArr.add(state);

            varibalesOrder.add("categories", catArr);
            return varibalesOrder;
        }

        @NotNull
        private JsonObject createKitchenVariables() {
            JsonObject varibalesOrder = new JsonObject();

            //TEST POBOČKA 476
            //POD hrádkem 1 DEV
            JsonArray branches = new JsonArray();
            JsonPrimitive branchID = new JsonPrimitive(476);
            branches.add(branchID);
            varibalesOrder.add("branches", branches);

            JsonArray categories = new JsonArray();
            JsonPrimitive category = new JsonPrimitive(String.valueOf(OrderRecipeStateEnum.safeValueOf("PREPARING")));
            categories.add(category);
            varibalesOrder.add("categories", categories);

            JsonArray sectors = new JsonArray();
            JsonPrimitive sector = new JsonPrimitive(687);
            sectors.add(sector);
            varibalesOrder.add("sectors",sectors);

            return varibalesOrder;
        }

        private final WebSocketCallback callback = new WebSocketCallback() {
            @Override
            public void recievedCallBack(Object result, SubscriptionType type) {
                if (result != null) {
                    switch (type) {
                        case ORDER_SUB: {
                            UpdatedOrderCountSubscription.Data data = (UpdatedOrderCountSubscription.Data) result;
                            break;
                        }
                        case KITCHEN_SUB: {
                            UpdatedKitchenRecipesSubscription.Data data = (UpdatedKitchenRecipesSubscription.Data) result;
                            break;
                        }
                    }
                }
                Log.d("SUB - RECEIVED", type.toString());
            }

            @Override
            public void connectedCallBack() {
                Log.d("Navázání spojení", "jooo fakt");

            }

            @Override
            public void disconnectedCallBack() {
                Log.d("Výpadek spojení", "jooo fakt");
            }
        };
    }
}