package SpringBoot.controller;

import SpringBoot.entity.*;
import SpringBoot.service.GoodService;
import SpringBoot.service.HotprodListService;
import SpringBoot.service.PicsService;
import SpringBoot.service.ShopService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.renren.common.utils.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import SpringBoot.service.ShopShowService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ImageController {
    @GetMapping(value = "/images", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage() throws IOException {
        // 从classpath中读取图片
        String image ="1.png";
        Resource resource = new ClassPathResource("/static/"+image );
        return StreamUtils.copyToByteArray(resource.getInputStream());
    }
    @Autowired
    private GoodService goodService;
    @Autowired
    private PicsService picsService;

    @GetMapping(value = "/goodCart/{id}")
    @ResponseBody
    public Message getMessage(@PathVariable("id") String id){
        GoodEntity good = goodService.getGood(id);
        List<PicsEntity> picsList = picsService.getPicsList(id);
        Result result = new Result();
        result.setCode(200);
        result.setMsg("成功");
        System.out.println(id);
        Message message = new Message();
        message.setGoodEntity(good);
        message.setPicsEntity(picsList);
        message.setResult(result);
        return message;
    }
    @Autowired
    private ShopShowService service;


    @GetMapping(value = "/roll/{id}")
    @ResponseBody
    public Map<String,Object> getMessage2(@PathVariable("id") String id){
        List<ShopShowEntity> byIdList = service.getByIdList(id);
        Map<String,Object> map = new HashMap<>();
        map.put("rollList",byIdList);
        return map;
    }



    @Autowired
    private HotprodListService hotprodListService;
    @GetMapping("/hotGood")
    @ResponseBody
    public Map<String,Object> getMessage2(){
        List<HotprodListEntity> all = hotprodListService.getAll();
        System.out.println(all);
        Map<String,Object> map = new HashMap<>();
        map.put("hotProdList",all);
        Result<Object> ok = new Result<>().ok("请求成功");
        ok.setCode(200);
        map.put("Message",ok);
        return map;
    }
    @GetMapping("/getImage/{url}")
    public void  get(@PathVariable("url") String url, HttpServletResponse response) throws WriterException, IOException {
        Map map = new HashMap();
//        获取文本内容
//        生成二维码
//        生成误差矫正
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
//        设置二维码字符集
        map.put(EncodeHintType.CHARACTER_SET,"utf-8");

//        生成留白
        map.put(EncodeHintType.MARGIN,1);
//        zxing的核心对象
//        多格式写入,通过对象生成二维码
        MultiFormatWriter writer = new MultiFormatWriter();
//        一个内容
//        第二个二维码格式，宽度
//        实际是一个二维数组
        BitMatrix encode = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300, map);
        int width = encode.getWidth();
        int height = encode.getHeight();
//        生成二维码图片
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//遍历未居中
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i,j,encode.get(i,j)? 0xFF000000: 0xFFFFFFFF);
            }
        }
//        返回图片
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(bufferedImage,"png",outputStream);
        outputStream.flush();
        outputStream.close();
    }









}
