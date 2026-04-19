package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpaceXEvent(
        int id,
        String title,
        @JsonProperty("event_date_utc") String eventDataUtc,
        @JsonProperty("event_date_unix") String eventDataUnix,
        @JsonProperty("flight_number") int flightNumber,
        String details,
        Links links
) {
}
