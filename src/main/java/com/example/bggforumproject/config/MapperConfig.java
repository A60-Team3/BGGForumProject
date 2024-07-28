package com.example.bggforumproject.config;

import com.example.bggforumproject.dtos.*;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.models.enums.ReactionType;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.record.RecordModule;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class MapperConfig {


    public MapperConfig() {

    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        TypeMap<User, UserOutDTO> userOutMap = modelMapper.createTypeMap(User.class, UserOutDTO.class);
        TypeMap<User, UserBlockOutDTO> userBlockOutDTOTypeMap = modelMapper.createTypeMap(User.class, UserBlockOutDTO.class);
        TypeMap<Post, PostAnonymousOutDTO> postAnonymousOutDTOTypeMap = modelMapper.createTypeMap(Post.class, PostAnonymousOutDTO.class);
        TypeMap<Post, PostOutFullDTO> postOutFullMap = modelMapper.createTypeMap(Post.class, PostOutFullDTO.class);
        TypeMap<Comment, CommentOutDTO> commentOutDTOTypeMap = modelMapper.createTypeMap(Comment.class, CommentOutDTO.class);
        TypeMap<Reaction, ReactionOutDTO> reactionOutDTOTypeMap = modelMapper.createTypeMap(Reaction.class, ReactionOutDTO.class);
        TypeMap<Tag, TagsOutDTO> tagsOutDTOTypeMap = modelMapper.createTypeMap(Tag.class, TagsOutDTO.class);

        userBlockOutDTOTypeMap.addMappings(mapper -> {
            mapper.using(toFullName).map(src -> src, UserBlockOutDTO::setFullName);
        });

        userOutMap.addMappings(mapper -> {
            mapper.using(toFullName).map(src -> src, UserOutDTO::setFullName);
            mapper.using(toCreatedOn).map(User::getRegisteredAt, UserOutDTO::setRegisteredAt);
            mapper.using(toCreatedOn).map(User::getUpdatedAt, UserOutDTO::setUpdatedAt);
            mapper.using(toRole).map(User::getRoles, UserOutDTO::setRoles);

        });

        tagsOutDTOTypeMap.addMappings(mapper -> {
            mapper.using(toPost).map(Tag::getPosts, TagsOutDTO::setPosts);
        });

        reactionOutDTOTypeMap.addMappings(mapper -> {
            mapper.using(toFullName).map(Reaction::getUserId, ReactionOutDTO::setReactionMakerFullName);
        });

        commentOutDTOTypeMap.addMappings(mapper -> {
            mapper.using(toSinglePost).map(Comment::getPostId, CommentOutDTO::setPostTitle);
            mapper.using(toFullName).map(Comment::getUserId, CommentOutDTO::setUserFullName);
            mapper.using(toCreatedOn).map(Comment::getCreatedAt, CommentOutDTO::setCreatedAt);
            mapper.using(toCreatedOn).map(Comment::getUpdatedAt, CommentOutDTO::setUpdatedAt);
        });

        postOutFullMap.addMappings(mapper -> {
            mapper.using(toTags).map(Post::getTags, PostOutFullDTO::setTags);
            mapper.using(toCreatedOn).map(Post::getCreatedAt, PostOutFullDTO::setCreatedAt);
            mapper.using(toCreatedOn).map(Post::getUpdatedAt, PostOutFullDTO::setUpdatedAt);
            mapper.using(toFullName).map(Post::getUserId, PostOutFullDTO::setPublisherName);

        });

        postAnonymousOutDTOTypeMap.addMappings(mapper -> {
            mapper.using(toTags).map(Post::getTags, PostAnonymousOutDTO::setTags);
            mapper.using(toFullName).map(Post::getUserId, PostAnonymousOutDTO::setUserFullName);
            mapper.using(toCreatedOn).map(Post::getCreatedAt, PostAnonymousOutDTO::setCreatedAt);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return context.getSource().format(formatter);
        }
    };

    Converter<Set<Tag>, Set<String>> toTags = new Converter<Set<Tag>, Set<String>>() {
        public Set<String> convert(MappingContext<Set<Tag>, Set<String>> context) {
            return context.getSource().stream().map(Tag::getName).collect(Collectors.toSet());
        }
    };

    Converter<Set<Post>, List<String>> toPost = new Converter<Set<Post>, List<String>>() {
        public List<String> convert(MappingContext<Set<Post>, List<String>> context) {
            return context.getSource().stream().map(Post::getTitle).collect(Collectors.toList());
        }
    };

    Converter<Post, String> toSinglePost = new Converter<Post, String>() {
        public String convert(MappingContext<Post, String> context) {
            return context.getSource().getTitle();
        }
    };

    Converter<Set<Role>, Set<String>> toRole = new Converter<Set<Role>, Set<String>>() {
        public Set<String> convert(MappingContext<Set<Role>, Set<String>> context) {
            return context.getSource().stream().map(Role::getAuthority).collect(Collectors.toSet());
        }
    };

    Converter<Set<Reaction>, Long> toReactionsLikes = new Converter<Set<Reaction>, Long>() {
        public Long convert(MappingContext<Set<Reaction>, Long> context) {
            return context
                    .getSource()
                    .stream()
                    .filter(reaction -> reaction.getReactionType().equals(ReactionType.LIKE))
                    .count();
        }
    };
}
