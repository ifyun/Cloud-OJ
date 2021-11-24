import {PagedData, User, UserInfo} from "@/api/type"
import {ApiPath} from "@/api/request/index"
import {buildHeaders, returnError} from "@/api/utils"
import axios from "axios"

const UserApi = {
    /**
     * 获取所有用户
     * @param page 页数
     * @param limit 每页数量
     * @param params 搜索参数
     * @param userInfo {@link UserInfo}
     */
    getAll(page: number, limit: number, params: any, userInfo: UserInfo): Promise<PagedData<User>> {
        return new Promise<PagedData<User>>((resolve, reject) => {
            axios({
                url: ApiPath.USER_ADMIN,
                method: "GET",
                headers: buildHeaders(userInfo),
                params: {
                    page,
                    limit,
                    userId: params.userId,
                    name: params.name
                }
            }).then((res) => {
                resolve(res.status === 200 ? res.data : {data: [], count: 0})
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    }
}

export default UserApi