package cz.deepvision.websocket.grahpql;

import com.google.gson.JsonObject;


public class VariablesContainer {
    protected SubscriptionType subscriptionType;
    protected Class<?> subscriptionClass;
    protected JsonObject variables;
    protected String subscriptionDataClassName;
    protected String graphqlQuery;

    protected VariablesContainer(SubscriptionType subscriptionType, Class<?> subscriptionClass, JsonObject variables, String subscriptionDataClassName, String graphqlQuery) {
        this.subscriptionType = subscriptionType;
        this.subscriptionClass = subscriptionClass;
        this.variables = variables;
        this.subscriptionDataClassName = subscriptionDataClassName;
        this.graphqlQuery = graphqlQuery;
    }

    protected SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    protected void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    protected Class<?> getSubscriptionClass() {
        return subscriptionClass;
    }

    protected void setSubscriptionClass(Class<?> subscriptionClass) {
        this.subscriptionClass = subscriptionClass;
    }

    protected JsonObject getVariables() {
        return variables;
    }

    protected void setVariables(JsonObject variables) {
        this.variables = variables;
    }

    protected String getSubscriptionDataClassName() {
        return subscriptionDataClassName;
    }

    protected void setSubscriptionDataClassName(String subscriptionDataClassName) {
        this.subscriptionDataClassName = subscriptionDataClassName;
    }

    protected String getGraphqlQuery() {
        return graphqlQuery;
    }

    protected void setGraphqlQuery(String graphqlQuery) {
        this.graphqlQuery = graphqlQuery;
    }
}
