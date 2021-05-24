package com.example.websockettest;

import com.google.gson.JsonObject;


public class VariablesContainer {
    private SubscriptionType subscriptionType;
    private Class<?> subscriptionClass;
    private JsonObject variables;
    private String subscriptionDataClassName;
    private String graphqlQuery;

    public VariablesContainer(SubscriptionType subscriptionType, Class<?> subscriptionClass, JsonObject variables, String subscriptionDataClassName, String graphqlQuery) {
        this.subscriptionType = subscriptionType;
        this.subscriptionClass = subscriptionClass;
        this.variables = variables;
        this.subscriptionDataClassName = subscriptionDataClassName;
        this.graphqlQuery = graphqlQuery;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Class<?> getSubscriptionClass() {
        return subscriptionClass;
    }

    public void setSubscriptionClass(Class<?> subscriptionClass) {
        this.subscriptionClass = subscriptionClass;
    }

    public JsonObject getVariables() {
        return variables;
    }

    public void setVariables(JsonObject variables) {
        this.variables = variables;
    }

    public String getSubscriptionDataClassName() {
        return subscriptionDataClassName;
    }

    public void setSubscriptionDataClassName(String subscriptionDataClassName) {
        this.subscriptionDataClassName = subscriptionDataClassName;
    }

    public String getGraphqlQuery() {
        return graphqlQuery;
    }

    public void setGraphqlQuery(String graphqlQuery) {
        this.graphqlQuery = graphqlQuery;
    }
}
