package SpringBoot.controller;

import SpringBoot.Tool.QRCodeUtils;
import SpringBoot.entity.VolunteerActivity;
import SpringBoot.service.impl.VolunteerActivityServiceImpl;
import io.renren.common.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import  SpringBoot.service.VolunteerActivityService;
@RestController
@RequestMapping("/api/activity")
public class VolunteerActivityController {

    /**
     * 发布志愿活动
     * @param activity 活动信息
     * @return 操作结果
     */
    @Resource
    private VolunteerActivityService activityService;


    @PostMapping("/publish")
    public Map<String, Object> publishActivity(@RequestBody VolunteerActivity activity) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 生成活动ID
            String activityId = UUID.randomUUID().toString().replace("-", "");
            activity.setActivityId(activityId);
            // 2. 设置默认值
            activity.setStatus(1); // 默认状态：未开始
            activity.setCreateTime(new java.util.Date());
            activity.setUpdateTime(new java.util.Date());
            System.out.println("ok");
            // 3. 调用数据库操作（由用户自己实现）
            boolean insert = activityService.insert(activity);
            if (insert) {
                // 4. 生成活动二维码（Base64编码）
                String qrCodeBase64 = QRCodeUtils.generateWechatQRCode(activityId);
                result.put("code", 200);
                result.put("message", "活动发布成功");
                result.put("data", activityId);
//                result.put("qrCode", qrCodeBase64); // 添加二维码信息
            } else {
                result.put("code", 500);
                result.put("message", "活动发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "服务器内部错误");
        }

        return result;
    }

    @PostMapping("/getqrCode/{id}")
    public Map<String, Object> publishActivity(@PathVariable("id") String id) {
        Map<String, Object> result = new HashMap<>();
        VolunteerActivity volunteerActivity = activityService.getVolunteerActivity(id);
        String qrCodeBase64 = QRCodeUtils.ObjectQrCode(volunteerActivity,300,300);
        result.put("code", 200);
        result.put("message", "活动发布成功");
        result.put("data", id);
        result.put("qrCode", qrCodeBase64); // 添加二维码信息
        return result;
    }


    @PostMapping("/getActivity")
    public Result get(){
        List<VolunteerActivity> allActivity = activityService.getAllActivity();
        Result<Object> objectResult = new Result<>();
        objectResult.setData(allActivity);
        objectResult.setMsg("请求成功");
        return objectResult;
    }


}
