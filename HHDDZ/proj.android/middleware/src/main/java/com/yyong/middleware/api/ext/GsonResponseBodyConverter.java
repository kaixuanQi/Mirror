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
package com.yyong.middleware.api.ext;


import com.yyong.middleware.api.DecryptData;
import com.yyong.middleware.api.DecryptKey;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.zero.support.work.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, Response<T>> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type response) {
        this.gson = gson;
        this.adapter = adapter;
        this.type = response;
    }

    @Override
    public Response<T> convert(ResponseBody value) throws IOException {
        try {
            String response = value.string();
            String json = response;
            JSONObject object = null;
            int code = 0;
            String message = null;

            if (type instanceof Class && DecryptData.class.isAssignableFrom((Class<?>) type)) {
                DecryptKey keyAno = ((Class<?>) type).getAnnotation(DecryptKey.class);
                String key = null;
                if (keyAno != null) {
                    key = keyAno.value();
                }
                if (key != null) {
//                    json = Decrypt.decrypt(response, key, "utf-8");
                }
            }
            try {
                object = new JSONObject(json);

                if (object.has("code")) {
                    code = object.optInt("code", 0);
                    if (code == 1) {
                        code = 0;
                    }
                    if (object.has("message")) {
                        message = object.optString("message", null);
                    } else if (object.has("msg")) {
                        message = object.optString("msg", null);
                    }
                    Object o = object.opt("data");
                    json = o == null ? null : o.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (this.type == String.class) {
                return (Response<T>) Response.error(code, message, json);
            }
            T t;
            if (json != null) {
                t = adapter.fromJson(json);
            } else {
                t = null;
            }
            return Response.error(code, message, t);
        } finally {
            value.close();
        }
    }
}
