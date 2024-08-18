package com.example.bggforumproject.helpers;

import com.example.bggforumproject.models.enums.ReactionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReactionTypeConverter implements Converter<String, ReactionType> {
    @Override
    public ReactionType convert(String source) {
        try {
            return ReactionType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Or handle the exception as needed
        }
    }
}

