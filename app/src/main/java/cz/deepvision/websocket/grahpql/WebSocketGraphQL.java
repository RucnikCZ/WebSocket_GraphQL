package cz.deepvision.websocket.grahpql;

import android.util.Log;

import com.apollographql.apollo.api.Subscription;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class WebSocketGraphQL {
    private final int TIMEOUT = 4000;
    private final int RECONNECT_TIME = 6000;
    private final ArrayList<VariablesContainer> operationsContainer;
    private final WebSocketCallback actionCallback;
    private final String token;
    private final String wssUrl;
    private final Gson gson;
    private final String appTag;

    private Timer timer = new Timer();
    private WebSocket ws = null;

    private final Boolean log;
    private final Boolean automaticReconnect;
    private Boolean isWebSocketInitialized = false;


    /**
     * @param token              User token for listening on webSocket
     * @param wssUrl             Websocket url to connect with server
     * @param variables          JSON variables for operations - MUST BE SAME SIZE AS OPERATIONS
     * @param callback           Place- where all responses will come
     * @param operations         Subscriptions to listen to, with its type - MUST BE SAME SIZE AZ VARIABLES
     * @param log                Enables/disables logging trought library
     * @param automaticReconnect Set true, to allow automatic reconnection after connection lost, or long ping interval
     */
    public WebSocketGraphQL(String token, String wssUrl, JsonObject[] variables, WebSocketCallback callback, HashMap<SubscriptionType, Class<?>> operations, String appTag, boolean log, boolean automaticReconnect) {
        this.token = token;
        this.wssUrl = wssUrl;
        this.actionCallback = callback;
        this.operationsContainer = new ArrayList<>();
        this.gson = new Gson();
        this.appTag = appTag;
        this.log = log;
        this.automaticReconnect = automaticReconnect;
        this.init(operations, variables);
    }

    private void init(HashMap<SubscriptionType, Class<?>> operations, JsonObject[] variables) {
        int index = 0;
        for (SubscriptionType subscriptionType : operations.keySet()) {
            if (!com.apollographql.apollo.api.Subscription.class.isAssignableFrom(operations.get(subscriptionType))) {
                throw new IllegalArgumentException("Not allowed graphql aClass");
            }
            Class<Subscription> subscriptionClass = (Class<Subscription>) operations.get(subscriptionType);
            try {
                String graphqlQuery = String.valueOf(subscriptionClass.getDeclaredField("QUERY_DOCUMENT").get(null));
                String subscriptionDataClassName = subscriptionClass.getName() + "$" + "Data";

                VariablesContainer cont = new VariablesContainer(subscriptionType, operations.get(subscriptionType), variables[index], subscriptionDataClassName, graphqlQuery);
                operationsContainer.add(cont);
                index++;
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void setupWebSocket() {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        try {
            ws = webSocketFactory.createSocket(wssUrl + token, TIMEOUT);
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                    super.onStateChanged(websocket, newState);
                    if (log)
                        Log.d(appTag, "State changed: " + newState.name());

                    if (newState == WebSocketState.CLOSED) {
                        if (automaticReconnect) {
                            if (timer != null) timer.cancel();
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        ws = websocket.recreate(TIMEOUT).connect();
                                        if (timer != null) {
                                            timer.cancel();
                                            timer.purge();
                                            timer = null;
                                        }
                                    } catch (WebSocketException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, RECONNECT_TIME);
                        }
                    }
                    if (newState == WebSocketState.OPEN) {
                        if (timer != null) {
                            timer.cancel();
                            timer.purge();
                            timer = null;
                        }
                    }
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    actionCallback.connectedCallBack();
                    initWebSocket(websocket);
                    if (log)
                        Log.d(appTag, "Connected");

                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    if (text.contains("ping")) {
                        if (log) Log.d(appTag, "Ping response: " + text);
                        if (automaticReconnect) CustomTimer.getInstance().checkTimer(websocket, RECONNECT_TIME);
                    } else {
                        if (log) Log.d(appTag, text);
                    }
                    if ((gson.fromJson(text, JsonObject.class).getAsJsonObject("message") != null) &&
                            ((gson.fromJson(text, JsonObject.class).getAsJsonObject("message").getAsJsonObject("result") != null)) &&
                            (gson.fromJson(text, JsonObject.class).getAsJsonObject("message").getAsJsonObject("result").getAsJsonObject("data") != null)) {
                        JsonObject data = gson.fromJson(text, JsonObject.class).getAsJsonObject("message").getAsJsonObject("result").getAsJsonObject("data");
                        for (VariablesContainer variablesContainer : operationsContainer) {
                            try {
                                Object result = gson.fromJson(data, Class.forName(variablesContainer.getSubscriptionDataClassName()));
                                actionCallback.recievedCallBack(result, variablesContainer.getSubscriptionType());
                            } catch (Exception e) {
                                Log.e(appTag, e.getLocalizedMessage());
                            }
                        }

                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws
                        Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    if (log) Log.i(appTag, "Disconnected");
                    actionCallback.disconnectedCallBack();
                }
            }).addProtocol("actioncable-v1-json");

            ws.addProtocol("actioncable-unsupported");
            ws.addHeader("Origin", "https://pos.speedlo.cloud");
            ws.addHeader("Referer", "SPEEDLO_WEBSOCKET_" + appTag);
            ws.setPingSenderName("WebSocketClient");
            ws.connect();

            initWebSocket(ws);

        } catch (IOException | WebSocketException | JSONException | InterruptedException e) {
            e.printStackTrace();
            actionCallback.disconnectedCallBack();
        }
    }

    public void unSubscribe(String reason) {
        if (ws != null) {
            if (log) Log.i(appTag, reason);
            ws.disconnect(reason);
        }
    }

    private synchronized void initWebSocket(WebSocket ws) throws JSONException, InterruptedException {
        if (!isWebSocketInitialized) {
            Log.d(appTag, "Web socket init");

            String identifier = UUID.randomUUID().toString();
            String openChannelString = "{\"command\":\"subscribe\",\"identifier\":\"{\\\"channel\\\":\\\"GraphqlChannel\\\",\\\"channelId\\\":\\\"" + identifier + "\\\"}\"}";
            JSONObject openChannelJson = new JSONObject(openChannelString);
            ws.sendText(openChannelJson.toString());
            for (VariablesContainer variablesContainer : operationsContainer) {

                Thread.sleep(5000);
                ws.sendText(generateJsonStructure(variablesContainer.getGraphqlQuery(), variablesContainer.getVariables(), identifier));
            }
            isWebSocketInitialized = true;
        }
    }

    private String generateJsonStructure(String query, JsonObject variables, String uuid) {
        JsonObject base = new JsonObject();
        base.addProperty("command", "message");

        JsonObject identifier = new JsonObject();
        identifier.addProperty("channel", "GraphqlChannel");
        identifier.addProperty("channelId", uuid);
        base.addProperty("identifier", identifier.toString());

        JsonObject data = new JsonObject();
        data.addProperty("query", query);
        data.add("variables", variables);
        data.addProperty("action", "execute");

        base.addProperty("data", data.toString());
        return base.toString();

    }
}
