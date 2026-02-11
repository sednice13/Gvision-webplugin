package com.gvision.gwebplugin.Commands.GwebCommandFolder;

public enum GwebChoises {
    MODIFY("modify"),
    RELOAD("reload"),
    TURNON("turnon"),
    TURNOFF("turnoff"),
    INVALID("invalid"),
    BAN("ban"),
    UNBAN("unban");

    private final String token;

    GwebChoises(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static GwebChoises fromString(String value) {
        if (value == null) {
            return INVALID;
        }
        String normalized = value.trim().toLowerCase();
        for (GwebChoises choice : values()) {
            if (choice.token.equals(normalized)) {
                return choice;
            }
        }
        return INVALID;
    }
}
