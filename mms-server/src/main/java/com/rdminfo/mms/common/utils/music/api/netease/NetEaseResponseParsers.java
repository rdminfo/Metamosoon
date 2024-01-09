package com.rdminfo.mms.common.utils.music.api.netease;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rdminfo.mms.common.entity.MmsFiles;
import com.rdminfo.mms.common.utils.music.api.ResponseParser;

import java.lang.reflect.Type;

/**
 * NetEase返回数据解析器
 *
 * @author rdminfo 2023/12/04 10:29
 */
public class NetEaseResponseParsers {

    public static final TestParser TEST_RES_PARSER = new TestParser();

    private static final class TestParser extends AbstractResultParser<MmsFiles> {
        @Override
        public NetEaseResponse<MmsFiles> parse(String response) {
            return super.doParse(response, new TypeToken<NetEaseResponse<?>>() {}.getType());
        }
    }

    public static abstract class AbstractResultParser<T> implements ResponseParser<NetEaseResponse<T>> {
        public AbstractResultParser() {}

        protected NetEaseResponse<T> doParse(String response, Type typeToken) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.fromJson(response, typeToken);
        }
    }

}
