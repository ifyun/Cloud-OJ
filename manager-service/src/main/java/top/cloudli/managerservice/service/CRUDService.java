package top.cloudli.managerservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import top.cloudli.managerservice.model.PagedResult;

import java.util.List;

public interface CRUDService {

    default ResponseEntity<?> buildGETSingleResponse(Object object) {
        return object != null ?
                ResponseEntity.ok(object) :
                ResponseEntity.noContent().build();
    }

    default ResponseEntity<PagedResult<List<?>>> buildGETResponse(PagedResult<List<?>> result) {
        return result.getData().size() > 0 ?
                ResponseEntity.ok(result) :
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
