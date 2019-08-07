package RMTwo.helper;

public enum  EventType {

    SEMINAR("S"), CONFERENCE("C"), TRADESHOW("T");
    private String eventTypePrefix;

    EventType(final String eventTypePrefix) {
        this.eventTypePrefix = eventTypePrefix;
    }

    public static EventType getEventType(String text) {
        EventType event = isValidEventType(text);
        return event;

    }

    public static EventType isValidEventType(final String event) {
        for (EventType s : EventType.values()) {
            if (s.toString().equalsIgnoreCase(event))
                return s;
        }
        return null;
    }

    public String getEventTypePrefix() {
        return eventTypePrefix;
    }

    public static EventType isValidEventTypePrefix(final String event) {
        for (EventType s : EventType.values()) {
            if (s.getEventTypePrefix().equalsIgnoreCase(event))
                return s;
        }
        return null;
    }
}
