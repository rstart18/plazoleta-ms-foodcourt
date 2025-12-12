package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedPlateResponse {
    private List<PlateListResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}