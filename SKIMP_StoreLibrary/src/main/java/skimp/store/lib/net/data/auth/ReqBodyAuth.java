package skimp.store.lib.net.data.auth;

import com.google.gson.annotations.SerializedName;

/**
 * API ID	API 명	URI	METHOD	Parameter (body)
 * 				head/body	NAME	TYPE	DESC
 * 공통(모든 API에서 공통처리, head 영역)
 *
 * IF-SKIMP-0001	인증확인	/skimp/common/api/auth/SKIMP-0001	POST
 * body	            token	String
 * 					encPwd	String
 * 					osName	String	디바이스 명 또는 OS 명
 * 					osVer	String	디바이스 OS 버전
 * 					mdn	String	디바이스 MDN
 * 					pakgId	String	APP ID
 * 					binVer	String	APP 버전
 * 					regularityYN	String	정직원여부, 정직원Y, 협력직원N
 * 				    platIdx = "1" (안드로이드: 1 , IOS: 2)
 */
public class ReqBodyAuth {
    @SerializedName("token")
    public String token;
    @SerializedName("encPwd")
    public String encPwd;
    @SerializedName("osName")
    public String osName;
    @SerializedName("osVer")
    public String osVer;
    @SerializedName("mdn")
    public String mdn;
    @SerializedName("pakgId")
    public String pakgId;
    @SerializedName("binVer")
    public String binVer;
    @SerializedName("regularityYN")
    public String regularityYN;
    @SerializedName("platIdx")
    public String platIdx;
}