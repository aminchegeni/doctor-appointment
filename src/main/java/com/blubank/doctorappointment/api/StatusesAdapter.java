package com.blubank.doctorappointment.api;

import com.blubank.doctorappointment.commons.model.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class StatusesAdapter implements Converter<String, EnumSet<Status>> {

    @Override
    public EnumSet<Status> convert(String source) {
        if (source.charAt(0) == '!') {
            return Status.valueOf(source.substring(1).toUpperCase()).negate();
        }
        return EnumSet.of(Status.valueOf(source.toUpperCase()));
    }
}
