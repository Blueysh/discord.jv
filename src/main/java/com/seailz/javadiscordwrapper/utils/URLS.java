package com.seailz.javadiscordwrapper.utils;

import com.seailz.javadiscordwrapper.utils.version.APIVersion;

public final class URLS {

    public static final String BASE_URL = "https://discord.com/api/v" + APIVersion.getLatest().getCode();

    public static class POST {
        public static class INTERACTIONS {
            public static class COMMANDS {
            }
        }
    }

    public static class GET {

        public static class GATEWAY {
            public static String GET_GATEWAY_URL = "/gateway";
        }

        public static class APPLICATION {
            /**
             * Requests info about the current application from the API
             * Returns the bot's application object
             */
            public static String APPLICATION_INFORMATION = "/oauth2/applications/@me";
        }
    }

    public static class GATEWAY {
        public static String BASE_URL = "wss://gateway.discord.gg/?v=" + APIVersion.getLatest().getCode() + "&encoding=json";
    }

}