package com.rdminfo.mms.common.utils.music.api;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.utils.NetEaseParamEncryptUtil;
import com.rdminfo.mms.common.utils.music.api.netease.NetEaseResponse;
import com.rdminfo.mms.common.utils.music.api.netease.NetEaseResponseParsers;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 音乐api请求工具
 *
 * @author rdminfo 2023/12/04 10:25
 */
@Component
public class MusicApiRequestUtil {

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36";

    public NetEaseResponse<MmsFiles> netEastSearch(String searchKey) {
        Map<String,Object> forms = new HashMap<>();
        forms.put("s", searchKey); forms.put("offset",0); forms.put("limit",20); forms.put("type",1); forms.put("csrf_token","");
        return this.netEaseRequest(forms, NetEaseResponseParsers.TEST_RES_PARSER, "/search/suggest/web?csrf_token=");
    }


    public <T> T netEaseRequest(Map<String,Object> forms, ResponseParser<T> responseParser, String path) {
        NetEaseParamEncryptUtil.Data encryptData = NetEaseParamEncryptUtil.encrypt(JSONUtil.toJsonStr(forms));
        String body = "params=" + URLEncodeUtil.encodeAll(encryptData.getParams()) + "&encSecKey=" + URLEncodeUtil.encodeAll(encryptData.getEncSecKey());
        String response = HttpRequest.post("https://music.163.com/weapi" + path).header("User-Agent", USER_AGENT, true)
                .body(body, ContentType.FORM_URLENCODED.getValue()).execute().body();
        return responseParser.parse(response);
    }

}
