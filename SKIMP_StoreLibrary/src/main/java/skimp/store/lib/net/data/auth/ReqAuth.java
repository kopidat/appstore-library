package skimp.store.lib.net.data.auth;

import androidx.annotation.NonNull;

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
 */
public class ReqAuth {
//    @SerializedName("head")
//    public ReqHeadAuth head;

    @SerializedName("body")
    public ReqBodyAuth body;

    public ReqAuth() {
//        head = new ReqHeadAuth();
        body = new ReqBodyAuth();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.append("\n");
        stringBuilder.append("[body.token = " + body.token + "]\n");
        stringBuilder.append("[body.encPwd = " + body.encPwd + "]\n");
        stringBuilder.append("[body.osName = " + body.osName + "]\n");
        stringBuilder.append("[body.osVer = " + body.osVer + "]\n");
        stringBuilder.append("[body.mdn = " + body.mdn + "]\n");
        stringBuilder.append("[body.pakgId = " + body.pakgId + "]\n");
        stringBuilder.append("[body.binVer = " + body.binVer + "]\n");
        stringBuilder.append("[body.regularityYN = " + body.regularityYN + "]\n");
        stringBuilder.append("[body.platIdx = " + body.platIdx + "]\n");

        return stringBuilder.toString();
    }
}