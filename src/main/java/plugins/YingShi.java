package plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import controller.MainController;
import utils.ControllerFactory;
import utils.Util;

import java.util.HashMap;
import java.util.Map;

public class YingShi {
    private static final String HOST = "https://open.ys7.com";
    private final MainController mainController = (MainController) ControllerFactory.controllers.get(MainController.class.getSimpleName());
    private String appKey;

    private String appSecret;

    public YingShi(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public static void main(String[] args) {
        String appKey = "xxx";
        String appSecret = "xxx";
        YingShi yingShi = new YingShi(appKey, appSecret);
        Map<String, Object> resp = yingShi.getAccessToken();
        System.out.println(resp);
        JSONObject data = JSONObject.parseObject(resp.get("data").toString());
        String accessToken = data.get("accessToken").toString();
        Map<String, Object> resp2 = yingShi.search(accessToken);
        System.out.println(resp2);
    }

    public JSONObject getAccessToken() {
        String url = "https://open.ys7.com/api/lapp/token/get";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, String> data = new HashMap<>();
        data.put("appKey", this.appKey);
        data.put("appSecret", this.appSecret);
        String res = Util.doPost(url, headers, data);
        return JSONObject.parseObject(res);
    }

    public JSONObject search(String accessToken) {
        String url = "https://open.ys7.com/api/lapp/live/video/list";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, String> query = new HashMap<>();
        query.put("accessToken", accessToken);
        String res = Util.doPost(url, headers, query);
        return JSONObject.parseObject(res);
    }

    public void checkValid() {
        this.mainController.ysResult.appendText("开始验证api有效性\n");
        try {
            JSONObject resp = getAccessToken();
            System.out.println(resp);
            if (resp.get("code").equals("200")) {
                System.out.println("[+] api有效");
                this.mainController.ysResult.appendText("[+] api有效\n");
                String pretty = JSON.toJSONString(resp, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
                this.mainController.ysResult.appendText(pretty + "\n\n");
                this.mainController.ysSearch.setDisable(false);
            } else {
                System.out.println("[-] api无效");
                this.mainController.ysResult.appendText("[-] api无效\n\n");
                this.mainController.ysSearch.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println("[-] api无效");
            this.mainController.ysResult.appendText("[-] api无效\n\n");
            this.mainController.ysSearch.setDisable(true);
        }
    }

    public void checkSearch(String accessToken) {
        this.mainController.ysResult.appendText("开始验证搜索视频资源\n");
        try {
            JSONObject resp = search(accessToken);
            System.out.println(resp);
            if (resp.get("code").equals("200")) {
                System.out.println("[+] 获取到视频资源");
                this.mainController.ysResult.appendText("[+] 获取到视频资源\n");
                String pretty = JSON.toJSONString(resp, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
                this.mainController.ysResult.appendText(pretty + "\n\n");
            } else {
                System.out.println("[-] 未获取到视频资源");
                this.mainController.ysResult.appendText("[-] 未获取到视频资源\n\n");
            }
        } catch (Exception e) {
            System.out.println("[-] 未获取到视频资源");
            this.mainController.ysResult.appendText("[-] 未获取到视频资源\n\n");
        }
    }
}
