package skimp.store.lib.net.data.auth;

import com.google.gson.annotations.SerializedName;

/**
 * API_ATH 개별앱 인증 응답 head
 * NAME             TYPE	DESC        결과_예시
 * result_code              result_code	성공 : 200 그이외 실패
 * result_msg               result_msg	성공
 */
public class ResHeadAuth {
    @SerializedName("result_code")
    String result_code;
    @SerializedName("result_msg")
    String result_msg;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.append("\n");
        stringBuilder.append("[result_code = " + result_code + "]\n");
        stringBuilder.append("[result_msg = " + result_msg + "]\n");

        return stringBuilder.toString();
    }
}