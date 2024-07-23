package com.example.bggforumproject.config;

import com.example.bggforumproject.persistance.models.User;
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

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        TypeMap<User, UserOutDTO> typeMap = modelMapper.createTypeMap(User.class, UserOutDTO.class);

        typeMap.addMappings(mapper -> {
            mapper.using(toFullName).map(src -> src, UserOutDTO::setFullName);
            mapper.using(toCreatedOn).map(User::getRegisteredAt, UserOutDTO::setRegisteredAt);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return context.getSource().format(formatter);
        }
    };
}
