package com.blubank.doctorappointment.commons.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

import static com.blubank.doctorappointment.commons.model.Status.UNKNOWN;

@Converter
public class StatusConverter implements AttributeConverter<Status, Character> {

    @Override
    public Character convertToDatabaseColumn(Status attribute) {
        if (Objects.nonNull(attribute)) {
            return attribute.getCode();
        }
        return UNKNOWN.getCode();
    }

    @Override
    public Status convertToEntityAttribute(Character dbData) {
        if (Objects.nonNull(dbData)) {
            return Status.valueOf(dbData);
        }
        return UNKNOWN;
    }
}
