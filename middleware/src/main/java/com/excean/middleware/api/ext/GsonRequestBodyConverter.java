/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excean.middleware.api.ext;


import android.util.Log;

import com.excean.middleware.api.EncryptData;
import com.excean.middleware.api.EncryptKey;
import com.excean.middleware.api.FormData;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.zero.support.common.AppGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

public final class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private JSONObject params;

    GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
//        params = RequestParams.generate(AppGlobal.getApplication());
    }

    @Override
    public RequestBody convert(T value) throws IOException {

        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();
        String result = buffer.readByteString().string(UTF_8);

        if (value instanceof FormData) {
            try {
                JSONObject object = new JSONObject(result);
                Iterator<String> iterator = params.keys();
                FormBody.Builder builder = new FormBody.Builder();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    builder.addEncoded(key, String.valueOf(params.opt(key)));
                }
                iterator = object.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    builder.addEncoded(key, String.valueOf(object.opt(key)));
                }
                return builder.build();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (value instanceof EncryptData) {
            try {
                JSONObject object = new JSONObject(result);
                Iterator<String> iterator = params.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    object.put(key, params.opt(key));
                }
                result = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            EncryptKey data = value.getClass().getAnnotation(EncryptKey.class);
            if (data != null) {
                String key = data.value();
            }

//            result = AES.encryptToBase64(result);

        }
        return RequestBody.create(MEDIA_TYPE, result);
    }
}
