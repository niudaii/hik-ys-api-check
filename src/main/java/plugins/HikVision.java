package plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import controller.MainController;
import utils.ControllerFactory;
import utils.Util;

import java.util.HashMap;
import java.util.Map;

public class HikVision {
    private static final String ARTEMIS_PATH = "/artemis";
    private static final String Test = "/api/resource/v1/regions/regionIndexCode/cameras";
    private static final String GetRegions_V1_V2 = "/api/resource/v1/regions/root";
    private static final String CameraSearch_V1 = "/api/resource/v1/cameras";
    private static final String CameraSearch_V2 = "/api/resource/v2/camera/search";
    private static final String CameraPreview_V1 = "/api/video/v1/cameras/previewURLs";
    private static final String CameraPreview_V2 = "/api/video/v2/cameras/previewURLs";
    private static final String CameraPlayback_V1 = "/api/video/v1/cameras/playbackURLs";
    private static final String CameraPlayback_V2 = "/api/video/v2/cameras/playbackURLs";
    private final MainController mainController = (MainController) ControllerFactory.controllers.get(MainController.class.getSimpleName());


    public HikVision(String host, String appKey, String appSecret) {
        ArtemisConfig.host = host;
        ArtemisConfig.appKey = appKey;
        ArtemisConfig.appSecret = appSecret;
    }

    public static Map<String, Object> publicHkInterface(JSONObject jsonBody, String url) {
        String getCamsApi = ARTEMIS_PATH + url;
        Map<String, String> path = new HashMap<>(2);
        path.put("https://", getCamsApi);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, "application/json", null);
        return Util.getStringToMap(result);
    }

    public Map<String, Object> getRegions() {
        JSONObject jsonBody = new JSONObject();
        return publicHkInterface(jsonBody, GetRegions_V1_V2);
    }

    public Map<String, Object> searchV1() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo", 1);
        jsonBody.put("pageSize", 100);
        return publicHkInterface(jsonBody, CameraSearch_V1);
    }

    public Map<String, Object> searchV2() {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo", 1);
        jsonBody.put("pageSize", 100);
        return publicHkInterface(jsonBody, CameraSearch_V2);
    }

    public Map<String, Object> previewV1(String indexCode) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        return publicHkInterface(jsonBody, CameraPreview_V1);
    }

    public Map<String, Object> previewV2(String indexCode) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        return publicHkInterface(jsonBody, CameraPreview_V2);
    }

    public Map<String, Object> playbackV1(String indexCode) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        return publicHkInterface(jsonBody, CameraPlayback_V1);
    }

    public Map<String, Object> playbackV2(String indexCode) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("beginTime", "2022-11-28T13:46:59.932+08:00");
        jsonBody.put("endTime", "2022-11-28T13:50:59.932+08:00");
        return publicHkInterface(jsonBody, CameraPlayback_V2);
    }

    public void checkValid() {
        this.mainController.hikResult.appendText("[+] 开始验证api有效性\n");
        try {
            Map<String, Object> resp1 = getRegions();
            String msg1 = resp1.get("msg").toString();
            if (msg1.equalsIgnoreCase("SUCCESS")) {
                System.out.println("[+] api有效!");
                this.mainController.hikResult.appendText("[+] api有效!\n\n");
                this.mainController.hikSearch.setDisable(false);
            } else {
                System.out.println("[-] api无效!");
                this.mainController.hikResult.appendText("[-] api无效!\n\n");
                this.mainController.hikSearch.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println("[-] api无效!");
            this.mainController.hikResult.appendText("[-] api无效!\n\n");
            this.mainController.hikSearch.setDisable(true);
        }
    }

    public void checkSearch() {
        this.mainController.hikResult.appendText("[+] 开始验证搜索视频资源\n");
        try {
            Map<String, Object> resp1 = searchV1();
            Map<String, Object> resp2 = searchV2();
            if (resp1.containsKey("msg")) {
                CheckSearch(resp1);
                this.mainController.hikResult.appendText("[+] isc 平台 1.4 以下版本, api 为 " + CameraSearch_V1 + "\n\n");
            } else if (resp2.containsKey("msg")) {
                CheckSearch(resp2);
                this.mainController.hikResult.appendText("[+] isc 平台 1.4 及以上版本, api 为 " + CameraSearch_V2 + "\n\n");
            } else {
                System.out.println("[-] 未获取到视频资源");
                this.mainController.hikResult.appendText("[-] 未获取到视频资源\n\n");
                this.mainController.hikPreview.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println("[-] 未获取到视频资源");
            this.mainController.hikResult.appendText("[-] 未获取到视频资源\n\n");
            this.mainController.hikPreview.setDisable(true);
        }
    }

    private void CheckSearch(Map<String, Object> resp) {
        String msg2 = resp.get("msg").toString();
        if (msg2.equalsIgnoreCase("SUCCESS")) {
            System.out.println(resp.get("data"));
            System.out.println("[+] 获取到视频资源");
            String pretty = JSON.toJSONString(resp, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
            this.mainController.hikResult.appendText("[+] 获取到视频资源\n");
            this.mainController.hikResult.appendText(pretty + "\n");
            this.mainController.hikPreview.setDisable(false);
        }
    }

    public void checkPreview(String indexCode) {
        this.mainController.hikResult.appendText("[+] 开始获取预览地址\n");
        try {
            Map<String, Object> resp1 = previewV1(indexCode);
            Map<String, Object> resp2 = previewV2(indexCode);
            if (resp1.containsKey("msg")) {
                CheckPreview(resp1);
            } else if (resp2.containsKey("msg")) {
                CheckPreview(resp2);
            } else {
                System.out.println("[-] 未获取到预览地址");
                this.mainController.hikResult.appendText("[-] 未获取到预览地址\n\n");
            }
        } catch (Exception e) {
            System.out.println("[-] 未获取到预览地址");
            this.mainController.hikResult.appendText("[-] 未获取到预览地址\n\n");
        }
    }

    private void CheckPreview(Map<String, Object> resp) {
        String msg2 = resp.get("msg").toString();
        if (msg2.equalsIgnoreCase("success")) {
            System.out.println(resp.get("data"));
            System.out.println("[+] 获取到预览地址");
            String pretty = JSON.toJSONString(resp, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
            this.mainController.hikResult.appendText("[+] 获取到预览地址\n");
            this.mainController.hikResult.appendText(pretty + "\n\n");
        }
    }

    public void MainCheck() {
        getRegions();
        searchV1();
        searchV2();
//        previewV1("bb146b591ff440ee96c20897a3d94ec4");
//        previewV2("656180a94f464fb4ab77fff929cb3538");
//        playbackV1("ba070139de25443fbe4bec354b5d9605");
//        playbackV2("7c8fc6304fed4523b3bd60c7154596a7");
    }

    public static void main(String[] args) {
        String host;
        String appKey;
        String appSecret;

        host = "xxx:444";
        appKey = "xxx";
        appSecret = "xxx";

        HikVision app = new HikVision(host, appKey, appSecret);
        app.MainCheck();
    }
}
