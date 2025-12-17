package co.com.bancolombia.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TraceabilityApiResponse {
    private List<TraceabilityResponse> data;
}