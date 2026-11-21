
package com.sakura.languageschool.bean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import com.sakura.languageschool.model.Enrollment;
import com.sakura.languageschool.model.dto.EnrollmentResponse;

@Component
public class Mapper {

    private final ModelMapper modelMapper;

    public Mapper() {
        this.modelMapper = new ModelMapper();

        this.modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Enrollment -> EnrollmentResponse mapping
        this.modelMapper.typeMap(
                Enrollment.class, EnrollmentResponse.class
        ).addMappings(mapping ->
                mapping.skip(EnrollmentResponse::setReviewedBy)
        );
    }

    public <S, T> List<T> mapList(
            List<S> source, Class<T> targetClass) {

        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <S, T> List<T> mapSet(
            Set<S> source, Class<T> targetClass) {

        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public void map(Object source, Object destination) {
        modelMapper.map(source, destination);
    }

    public ModelMapper getMapper() {
        return modelMapper;
    }
}
