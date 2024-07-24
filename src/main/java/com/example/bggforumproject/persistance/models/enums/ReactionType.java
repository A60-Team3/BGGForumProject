package com.example.bggforumproject.persistance.models.enums;

public enum ReactionType {
    LIKE, DISLIKE;

    @Override
    public String toString() {
        switch (this) {
            case LIKE:
                return "LIKE";
            case DISLIKE:
                return "DISLIKE";
            default:
                return "UNKNOWN";
        }
    }
}
