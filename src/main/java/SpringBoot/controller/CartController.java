package SpringBoot.controller;

import SpringBoot.dto.ShopCollectDTO;
import SpringBoot.entity.CartShopEntity;
import SpringBoot.entity.GoodEntity;
import SpringBoot.entity.ShopCollectEntity;
import SpringBoot.service.impl.CartShopServiceImpl;
import SpringBoot.service.impl.ShopCollectServiceImpl;
import io.renren.common.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import  SpringBoot.service.ShopCollectService;
import  SpringBoot.service.CartShopService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CartController {
    @Resource
    private ShopCollectService shopCollectService;
    @PostMapping("/addCollect")
    public  Result<Object> add(@RequestBody ShopCollectEntity shopCollect){
        boolean insert = shopCollectService.insert(shopCollect);
        Result<Object> objectResult = new Result<>();
        if (insert){
            objectResult.setCode(200);
         return objectResult.ok("收藏成功");
        }
        objectResult.setCode(400);
        return objectResult.error("收藏失败");
    }
    @PostMapping("/deleteCollect")
    public  Result<Object> deleteCollect(@RequestBody ShopCollectEntity shopCollect){
      shopCollectService.deleteCollect(shopCollect);
      return new Result<>().ok("删除成功");
    }
    @PostMapping("/isCollect")
    public  Result<Object> isCollect(@RequestBody ShopCollectEntity shopCollect){
        Result result = new Result();

        boolean collect = shopCollectService.isCollect(shopCollect);
        if (collect){
            result.setCode(200);
            result.setData("已存在");
        }else {
            result.setCode(400);
            result.setData("不存在");
        }
        return result;
    }






    @PostMapping("/getCollectNumber")
    public Result<Object> getCollectNumber(@RequestBody ShopCollectEntity shopCollect)
    {
        Long numberById = shopCollectService.getNumberById(shopCollect);
        System.out.println(numberById);
        Result result = new Result();
        if (numberById <= 0 ){
            result.setCode(400);
            result.setData("无收藏");
        }else {
            result.setCode(400);
            result.setData(numberById);
        }
        return result;
    }
    @PostMapping("/getCollectGood")
    public Result<Object> getCollectNumber2(@RequestBody ShopCollectEntity shopCollect)
    {
        List<GoodEntity> goodCollect = shopCollectService.getGoodCollect(shopCollect);
        Result<Object> objectResult = new Result<>();
        objectResult.setCode(200);
        objectResult.setData(goodCollect);
        return objectResult;
    }

    @Resource
    private CartShopService cartShopService;

    @PostMapping("/addCart")
    public Result<Object> add(@RequestBody CartShopEntity cartShopEntity){
        cartShopService.addCart(cartShopEntity);
        return new Result<>().ok("成功");
    }
    @PostMapping("/getCount")
    private Result<Object> get(@RequestBody CartShopEntity cartShopEntity){

        Long count = cartShopService.getCount(cartShopEntity);
        Result<Object> objectResult = new Result<>();
        objectResult.setData(count);
        objectResult.setCode(200);
        return objectResult;
    }
    @PostMapping("/getCart/{id}")
    private  Result<Object> getCart(@PathVariable("id") Integer id){
        List<CartShopEntity> byUserId = cartShopService.getByUserId(id);
        Result<Object> objectResult = new Result<>();
        objectResult.setCode(200);
        objectResult.setData(byUserId);
        objectResult.setMsg("成功");
        return objectResult;
    }
    @PostMapping("/deleteCart")
    public Result delete(@RequestBody CartShopEntity cartShopEntity){
        boolean delete = cartShopService.delete(cartShopEntity);
        Result<Object> objectResult = new Result<>();
        if (delete){
            objectResult.setCode(200);
            return objectResult.ok("删除成功");
        }
        objectResult.setCode(400);
        return objectResult.error("删除失败");

    }









}
