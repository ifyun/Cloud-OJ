import {PagedData, Ranking} from "@/api/type"
import {ApiPath} from "@/api/request/index"
import {returnError} from "@/api/utils"
import axios from "axios"

const RankingApi = {
    get(page: number, limit: number): Promise<PagedData<Ranking>> {
        return new Promise<PagedData<Ranking>>((resolve, reject) => {
            axios({
                url: ApiPath.RANKING,
                method: "GET",
                params: {
                    page,
                    limit
                }
            }).then((res) => {
                if (res.status == 200) {
                    resolve(res.data as PagedData<Ranking>)
                } else {
                    resolve({data: [], count: 0})
                }
            }).catch((error) => {
                reject(returnError(error))
            })
        })
    }
}

export default RankingApi
