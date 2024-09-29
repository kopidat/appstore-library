package skimp.store.lib.net.data.auth;

import com.google.gson.annotations.SerializedName;

/**
 * API_ATH 개별앱 인증 요청 head
 * NAME     TYPE	DESC
 * token            token
 * encPwd           encPwd
 */
public class ReqHeadAuth {
    @SerializedName("token")
    public String token;
    @SerializedName("encPwd")
    public String encPwd;
}