package bg.exploreBG.model.dto;

import org.springframework.http.ResponseEntity;

import java.util.List;

public class ApiResponseCollection<T> {
    private final List<T> data;

    public ApiResponseCollection(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }
}
