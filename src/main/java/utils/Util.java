package utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Util {

    private static final CloseableHttpAsyncClient asyncClient;

    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(50000)
                .setSocketTimeout(50000)
                .setConnectionRequestTimeout(10)//设置为10ms
                .build();

        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();

        //设置连接池大小
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setMaxTotal(10);
        connManager.setDefaultMaxPerRoute(10);
        asyncClient = HttpAsyncClients.custom().
                setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static Map<String, Object> getStringToMap(String str) {
        return JSONArray.parseObject(str);
    }

    //POST请求
    public static String doPost(String url, Map<String, String> headers, Map<String, String> querys) {
        HttpPost post = new HttpPost();
        try {
            post.setURI(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException("请求失败", e);
        }
        Set<Map.Entry<String, String>> entries = null;
        if (headers != null && !headers.isEmpty()) {
            entries = headers.entrySet();
            for (Map.Entry<String, String> e : entries) {
                post.addHeader(e.getKey(), e.getValue());
            }
        }
        if (querys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            for (String key : querys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, querys.get(key)));
            }
            UrlEncodedFormEntity formEntity = null;
            try {
                formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("请求失败", e);
            }
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            post.setEntity(formEntity);
        }
        try {
            asyncClient.start();
            HttpResponse response = asyncClient.execute(post, null).get();
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("请求失败" + response);
            }
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("请求失败", e);
        }
    }
}
