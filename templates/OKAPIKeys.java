package cmanager;


public class OKAPIKeys
{
    public static String
    get_CONSUMER_API_KEY(final OKAPI.OKAPI_INSTALLATION okapiInstallation)
    {
        switch (okapiInstallation)
        {
        case DE:
            return "${oc_okapi_de_consumer_key}";
        case NL:
            return "${oc_okapi_nl_consumer_key}";
        case PL:
            return "${oc_okapi_pl_consumer_key}";
        case RO:
            return "${oc_okapi_ro_consumer_key}";
        case UK:
            return "${oc_okapi_uk_consumer_key}";
        case US:
            return "${oc_okapi_us_consumer_key}";
        default:
            throw new IllegalArgumentException();
        }
    }

    public static String
    get_CONSUMER_SECRET_KEY(final OKAPI.OKAPI_INSTALLATION okapiInstallation)
    {
        switch (okapiInstallation)
        {
        case DE:
            return "${oc_okapi_de_consumer_secret}";
        case NL:
            return "${oc_okapi_nl_consumer_secret}";
        case PL:
            return "${oc_okapi_pl_consumer_secret}";
        case RO:
            return "${oc_okapi_ro_consumer_secret}";
        case UK:
            return "${oc_okapi_uk_consumer_secret}";
        case US:
            return "${oc_okapi_us_consumer_secret}";
        default:
            throw new IllegalArgumentException();
        }
    }
}
