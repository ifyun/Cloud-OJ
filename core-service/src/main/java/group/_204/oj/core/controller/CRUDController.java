package group._204.oj.core.controller;

import group._204.oj.core.model.Msg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import group._204.oj.core.model.PagedResult;

import java.util.List;

/**
 * 增删改查控制器接口，提供方法用于构造返回数据
 */
public interface CRUDController {

    /**
     * 单条查询
     *
     * @param object 单条数据
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<?> buildGETResponse(Object object) {
        return object != null ?
                ResponseEntity.ok(object) :
                ResponseEntity.notFound().build();
    }

    /**
     * 多条查询
     *
     * @param data {@link List} 多条数据
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<?> buildGETResponse(List<List<?>> data) {
        return data.get(0).size() > 0 ?
                ResponseEntity.ok(new PagedResult(data.get(0), (Integer) data.get(1).get(0))) :
                ResponseEntity.noContent().build();
    }

    /**
     * 更新操作
     *
     * @param msg 消息
     * @return {@link ResponseEntity} 200：更新成功
     */
    default ResponseEntity<?> buildPUTResponse(Msg msg) {
        return ResponseEntity.status(msg.getStatus()).body(msg);
    }

    /**
     * 创建操作
     *
     * @param result 异常
     * @return {@link ResponseEntity} 201：已创建，400：请求数据不正确
     */
    default ResponseEntity<?> buildPOSTResponse(boolean result) {
        return result ?
                ResponseEntity.status(HttpStatus.CREATED).build() :
                ResponseEntity.badRequest().build();
    }

    /**
     * 删除操作
     *
     * @param msg 异常
     * @return {@link ResponseEntity} 204：删除成功
     */
    default ResponseEntity<?> buildDELETEResponse(Msg msg) {
        return ResponseEntity.status(msg.getStatus()).body(msg);
    }
}
