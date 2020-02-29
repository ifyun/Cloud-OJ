package top.cloudli.managerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import top.cloudli.managerservice.model.PagedResult;

import java.util.List;

public interface CRUDController {

    default ResponseEntity<?> buildGETResponse(Object object) {
        return object != null ?
                ResponseEntity.ok(object) :
                ResponseEntity.noContent().build();
    }

    default ResponseEntity<?> buildGETResponse(List<List<?>> data) {
        return data.get(0).size() > 0 ?
                ResponseEntity.ok(new PagedResult(data.get(0), (Long) data.get(1).get(0))) :
                ResponseEntity.noContent().build();
    }

    default ResponseEntity<Void> buildPUTResponse(boolean result) {
        return result ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    default ResponseEntity<Object> buildPOSTResponse(Object error) {
        return error == null ?
                ResponseEntity.status(HttpStatus.CREATED).build() :
                ResponseEntity.badRequest().body(error);
    }

    default ResponseEntity<Void> buildDELETEResponse(boolean result) {
        return result ?
                ResponseEntity.noContent().build() :
                ResponseEntity.status(HttpStatus.GONE).build();
    }
}
