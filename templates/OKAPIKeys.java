package cmanager;

public class OKAPIKeys {
    public static String get_CONSUMER_API_KEY() {
        return "${oc_okapi_de_consumer_key}";
    }

    public static String get_CONSUMER_SECRET_KEY() {
        return "${oc_okapi_de_consumer_secret}";
    }
}