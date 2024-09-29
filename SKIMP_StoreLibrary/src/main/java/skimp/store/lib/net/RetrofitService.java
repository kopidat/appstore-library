package skimp.store.lib.net;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import skimp.store.lib.net.data.auth.ReqAuth;
import skimp.store.lib.net.data.auth.ResAuth;

public interface RetrofitService {

    // 개별앱 인증

    @POST("/skimp/common/api/auth/SKIMP-0001")
    Call<ResAuth> auth(@Body ReqAuth appInfo);
}