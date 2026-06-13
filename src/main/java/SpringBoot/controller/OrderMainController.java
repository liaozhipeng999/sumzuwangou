package SpringBoot.controller;


import SpringBoot.dto.OrderMainDTO;
import SpringBoot.entity.*;
import SpringBoot.service.OrderMainService;
import io.renren.common.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Peng sunlightcs@gmail.com
 * @since 1.0.0 2025-10-26
 */
@RestController
public class OrderMainController {


    @Resource
    private OrderMainService orderMainService;

    @PostMapping("/pay")
    public Result pay(@RequestBody List<CartShopEntity> list){
        for (CartShopEntity cartShopEntity : list) {
            orderMainService.insertOrderMain(cartShopEntity);
        }
        Result<Object> objectResult = new Result<>();
        objectResult.setCode(200);
        objectResult.setData("ok");
        return objectResult;
    }
    @PostMapping("/getOrder/{id}")
    public Result<Object> get05(@PathVariable(value = "id") String id){
        Map<String, Object> orderMain = orderMainService.getOrderMain(id);

        System.out.println(orderMain);


        Result<Object> objectResult = new Result<>();
        objectResult.setCode(200);
        objectResult.setData(orderMain);
        return objectResult;

    }





}
