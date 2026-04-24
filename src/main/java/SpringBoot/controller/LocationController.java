package SpringBoot.controller;

import SpringBoot.Tool.Check;
import SpringBoot.dto.LocationDTO;
import SpringBoot.entity.LocationEntity;
import SpringBoot.service.impl.LocationServiceImpl;
import io.renren.common.utils.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import SpringBoot.service.LocationService;

import java.util.List;

@RestController
@Slf4j
public class LocationController {
    @Resource
    private LocationService locationService;

    @PostMapping("/insertLocation")
    public Result insertLocation(@RequestBody LocationEntity locationEntity){
        Result<Object> objectResult = new Result<>();


        System.out.println(locationEntity);

        if (!Check.validatePhone(String.valueOf(locationEntity.getPhone()))){
            objectResult.setCode(400);
            System.out.println(4);
            return objectResult.error("电话号码不对");
        }
        if (!Check.validateChineseName(locationEntity.getName())){
            objectResult.setCode(400);
            System.out.println(2);
            return objectResult.error("名字应该为中文名");
        }
        if (locationEntity.getState().equals("1")){
            System.out.println(3);
            locationService.updateSetZero(locationEntity);
        }
        locationService.insert(locationEntity);

        objectResult.setCode(200);
        return objectResult.ok("添加成功");
    }

    @PostMapping("/getAddress/{id}")
    public Result getAdd(@PathVariable("id") Integer id){
        Result<Object> objectResult = new Result<>();
        LocationEntity allLocation = locationService.getAllLocation(id);
        if (allLocation == null){
            objectResult.setCode(400);
            return objectResult.error("没有地址");
        }
        objectResult.setCode(200);
        objectResult.setData(allLocation);
        objectResult.setMsg("默认地址");
        return objectResult;
    }
   @GetMapping("/getAddress/{id}")
    public Result getAdd2(@PathVariable("id") Integer id){
        Result<Object> objectResult = new Result<>();

       List<LocationEntity> allLocation = locationService.getAllLocationByList(id);
       System.out.println(allLocation);
        if (allLocation == null){
            objectResult.setCode(400);
            return objectResult.error("没有地址");
        }
        log.info("抵达+");

        objectResult.setCode(200);
        objectResult.setData(allLocation);
        objectResult.setMsg("默认地址");
        return objectResult;
    }






}
