/*
 * Copyright (C) 2016. Tien Hoang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tientun.mockresponse;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 *
 */
public class FakeInterceptor implements Interceptor {
    private static final String TAG = FakeInterceptor.class.getSimpleName();
    private static final String FILE_EXTENSION = ".json";
    private Context mContext;

    private String mContentType = "application/json";

    public FakeInterceptor(Context context) {
        mContext = context;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    /**
     * Set content type for header
     *
     * @param contentType Content type
     * @return FakeInterceptor
     */
    public FakeInterceptor setContentType(String contentType) {
        mContentType = contentType;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        List<String> listSuggestionFileName = new ArrayList<>();
        String method = chain.request().method().toLowerCase();
        new AssetsFileScanner(mContext);
        Response response = null;
        // Get Request URI.
        final URI uri = chain.request().url().uri();
        Log.d(TAG, "--> Request url: [" + method.toUpperCase() + "]" + uri.toString());

        String defaultFileName = getFileName(chain);

        //create file name with http method
        //eg: getLogin.json
        listSuggestionFileName.add(method + upCaseFirstLetter(defaultFileName));

        //eg: login.json
        listSuggestionFileName.add(defaultFileName);

        String responseFileName = getFirstFileNameExist(listSuggestionFileName, uri);
        if (responseFileName != null) {
            String fileName = getFilePath(uri, responseFileName);
            Log.d(TAG, "Read data from file: " + fileName);
            try {
                InputStream is = mContext.getAssets().open(fileName);
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder responseStringBuilder = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    responseStringBuilder.append(line).append('\n');
                }
                Log.d(TAG, "Response: " + responseStringBuilder.toString());
                response = new Response.Builder()
                        .code(200)
                        .message(responseStringBuilder.toString())
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse(mContentType), responseStringBuilder.toString().getBytes()))
                        .addHeader("content-type", mContentType)
                        .build();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            for (String file : listSuggestionFileName) {
                Log.e(TAG, "File not exist: " + getFilePath(uri, file));
            }
            response = chain.proceed(chain.request());
        }

        Log.d(TAG, "<-- END [" + method.toUpperCase() + "]" + uri.toString());
        return response;
    }

    private String upCaseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getFirstFileNameExist(List<String> inputFileNames, URI uri) throws IOException {
        String mockDataPath = uri.getHost() + uri.getPath();
        mockDataPath = mockDataPath.substring(0, mockDataPath.lastIndexOf('/'));
        Log.d(TAG, "Scan files in: " + mockDataPath);
        //List all files in folder
        String[] files = mContext.getAssets().list(mockDataPath);
        for (String fileName : inputFileNames) {
            if (fileName != null) {
                for (String file : files) {
                    if (fileName.equals(file)) {
                        return fileName;
                    }
                }
            }
        }
        return null;
    }

    private String getFileName(Chain chain) {
        String fileName = chain.request().url().pathSegments().get(chain.request().url().pathSegments().size() - 1);
        return fileName.isEmpty() ? "index" + FILE_EXTENSION : fileName + FILE_EXTENSION;
    }

    private String getFilePath(URI uri, String fileName) {
        String path;
        if (uri.getPath().lastIndexOf('/') != uri.getPath().length() - 1) {
            path = uri.getPath().substring(0, uri.getPath().lastIndexOf('/') + 1);
        } else {
            path = uri.getPath();
        }
        return uri.getHost() + path + fileName;
    }

    private void getPostQuerries(Chain chain) throws IOException {
        Charset UTF8 = Charset.forName("UTF-8");
        RequestBody requestBody = chain.request().body();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);

        Charset charset = UTF8;
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        if (isPlaintext(buffer)) {
            Log.d("xxx-body", buffer.readString(charset));
        }
    }

}
