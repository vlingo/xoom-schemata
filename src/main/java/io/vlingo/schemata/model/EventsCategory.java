package io.vlingo.schemata.model;


/**
 * @author Chandrabhan Kumhar
 */
public class EventsCategory {
    public static final EventsCategory INSTANCE;

    static {
        EventsCategory var0 = new EventsCategory ();
        INSTANCE = var0;
    }

    public enum Category {
        Commands,
        Data,
        Documents,
        Envelopes,
        Events,
        Metadata,
        None
    }
}

