package com.blubank.doctorappointment.api;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Criteria {

    private Map<String, Object> values;

    private Map<String, Object> wheres;
}
