package com.example.bggforumproject.config;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.Tag;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.models.base.BaseEntity;
import com.example.bggforumproject.presentation.dtos.PostAnonymousOutDTO;
import com.example.bggforumproject.presentation.dtos.PostOutFullDTO;
import com.example.bggforumproject.presentation.dtos.UserOutDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.record.RecordModule;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        TypeMap<User, UserOutDTO> userOutMap = modelMapper.createTypeMap(User.class, UserOutDTO.class);
        TypeMap<Post, PostAnonymousOutDTO> postOutFullMap = modelMapper.createTypeMap(Post.class, PostAnonymousOutDTO.class);


        postOutFullMap.addMappings(mapper -> {
            mapper.using(toTags).map(Post::getTags, PostAnonymousOutDTO::setTags);
            mapper.using(toFullName).map(Post::getUserId, PostAnonymousOutDTO::setUserFullName);
        });


        userOutMap.addMappings(mapper -> {
            mapper.using(toFullName).map(src -> src, UserOutDTO::setFullName);
            mapper.using(toCreatedOn).map(User::getRegisteredAt, UserOutDTO::setRegisteredAt);
            mapper.using(toCreatedOn).map(User::getUpdatedAt, UserOutDTO::setUpdatedAt);
            mapper.using(toRole).map(User::getRoles, UserOutDTO::setRoles);

        });

        return modelMapper.registerModule(new RecordModule());
    }

    Converter<User, String> toFullName = new Converter<User, String>() {
        public String convert(MappingContext<User, String> context) {
            User user = context.getSource();
            return user.getFirstName() + " " + user.getLastName();
        }
    };

    Converter<LocalDateTime, String> toCreatedOn = new Converter<LocalDateTime, String>() {
        public String convert(MappingContext<LocalDateTime, String> context) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return context.getSource().format(formatter);
        }
    };

    Converter<Set<Tag>, Set<String>> toTags = new Converter<Set<Tag>, Set<String>>() {
        public Set<String> convert(MappingContext<Set<Tag>, Set<String>> context) {
            return context.getSource().stream().map(Tag::getName).collect(Collectors.toSet());
        }
    };

    Converter<Set<Role>, Set<String>> toRole = new Converter<Set<Role>, Set<String>>() {
        public Set<String> convert(MappingContext<Set<Role>, Set<String>> context) {
            return context.getSource().stream().map(Role::getAuthority).collect(Collectors.toSet());
        }
    };
}
