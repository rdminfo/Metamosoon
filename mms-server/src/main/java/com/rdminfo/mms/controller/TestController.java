package com.rdminfo.mms.controller;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.utils.NetEaseParamEncryptUtil;
import com.rdminfo.mms.common.utils.music.api.MusicApiRequestUtil;
import com.rdminfo.mms.common.utils.music.api.netease.NetEaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MusicApiRequestUtil musicApiRequestUtil;


    @GetMapping()
    public String getList() {
//        try {
//            MP3File file = new MP3File(new File("D:\\download/那年.mp3"));
//            ID3v1Tag res = file.getID3v1Tag();
//            ID3v24Tag res1 = file.getID3v2TagAsv24();
//            AbstractID3v2Tag res2 = file.getID3v2Tag();
//            Iterator<TagField> fields = res2.getFields();
//            while (fields.hasNext()) {
//                TagField field = fields.next();
//                String id = field.getId();
//                String ss = new String(field.toString());
//                System.out.println(id);
//                System.out.println(ss);
//            }
//            res2.setField(FieldKey.YEAR, "1111");
//            file.setID3v2Tag(res2);
//            try {
//                file.commit();
//            } catch (CannotWriteException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TagException e) {
//            e.printStackTrace();
//        } catch (ReadOnlyFileException e) {
//            e.printStackTrace();
//        } catch (CannotReadException e) {
//            e.printStackTrace();
//        } catch (InvalidAudioFrameException e) {
//            e.printStackTrace();
//        }
        return "result1";
    }

    @GetMapping("/searchEncrypt")
    public String get(@RequestParam String name) {
        Map<String,Object> forms = new HashMap<>();
        forms.put("s", name); forms.put("offset",0); forms.put("limit",20); forms.put("type",1); forms.put("csrf_token","");
        NetEaseParamEncryptUtil.Data encryptData = NetEaseParamEncryptUtil.encrypt(JSONUtil.toJsonStr(forms));
        return JSONUtil.toJsonStr(encryptData);
    }

    @GetMapping("/doSearch")
    public String doSearch(String name) {
        NetEaseResponse<MmsFiles> res = musicApiRequestUtil.netEastSearch(name);
        return "";
    }

    @PostMapping("/doSearch/test")
    public String doSearchTest(@RequestParam String params, @RequestParam String encSecKey) throws IOException {
        String body = "params=" + URLEncodeUtil.encodeAll(params) + "&encSecKey=" + URLEncodeUtil.encodeAll(encSecKey);
        HttpRequest request = HttpRequest.post("https://music.163.com/weapi/search/suggest/web?csrf_token=")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36", true)
                .body(body, ContentType.FORM_URLENCODED.getValue());
        HttpResponse response = request.execute();
        return response.body();
    }

}
