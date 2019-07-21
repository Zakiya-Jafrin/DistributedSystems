package helper;

public enum Role {

    CUSTOMER("C"), MANAGER("M");

    private String value;

    Role(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Role fromString(String text) {
        for (Role b : Role.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException(text);
    }
}

