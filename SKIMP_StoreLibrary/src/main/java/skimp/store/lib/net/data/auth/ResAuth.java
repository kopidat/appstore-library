package skimp.store.lib.net.data.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Return (body)
 * head/body	GROUP_NAME	NAME	TYPE	DESC	결과_예시
 * head		result_code		result_code	성공 : 200 그이외 실패
 * 		result_msg		result_msg	성공
 * body		status		status
 * 1000 정상 수행
 * 2000 인증 오류
 * 3000 ID 또는 비밀번호 오류
 * 4000 Major 업데이트된 App이 스토어에 배포된 상태
 * 5000 token 만료
 * 6000 token 값이 일치하지 않음
 * 7000 버전 확인 오류
 * 8000 로그아웃
 * 9000 서버 오류
 */
public class ResAuth {
    @SerializedName("head")
    private ResHeadAuth head;

    @SerializedName("body")
    private ResBodyAuth body;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.append("\n");
        stringBuilder.append("[head = " + head + "]\n");
        stringBuilder.append("[body = " + body + "]\n");

        return stringBuilder.toString();
    }

    public String getCode() {
        return head.result_code;
    }

    public String getMessage() {
        return  head.result_msg;
    }

    public String getStatus() {
        return body.status;
    }
}