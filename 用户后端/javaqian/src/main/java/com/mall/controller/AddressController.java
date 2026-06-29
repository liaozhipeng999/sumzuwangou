package com.mall.controller;

import com.mall.entity.UserAddress;
import com.mall.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @GetMapping("/list")
    public Map<String, Object> getAddressList(@RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", addresses);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAddressDetail(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserAddress address = userAddressMapper.selectById(id);
            if (address != null) {
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", address);
            } else {
                result.put("code", 404);
                result.put("message", "地址不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> createAddress(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserAddress address = new UserAddress();
            address.setUserId(params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : 1L);
            address.setName((String) params.get("receiver_name"));
            address.setPhone((String) params.get("receiver_phone"));
            address.setProvince((String) params.get("province"));
            address.setCity((String) params.get("city"));
            address.setDistrict((String) params.get("district"));
            address.setDetail((String) params.get("detail_address"));
            address.setIsDefault(params.get("is_default") != null ? Integer.valueOf(params.get("is_default").toString()) : 0);
            address.setStatus(1);
            address.setCreatedAt(LocalDateTime.now());
            address.setUpdatedAt(LocalDateTime.now());

            userAddressMapper.insert(address);

            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", address);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> updateAddress(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long id = Long.valueOf(params.get("id").toString());
            UserAddress address = userAddressMapper.selectById(id);
            if (address == null) {
                result.put("code", 404);
                result.put("message", "地址不存在");
                return result;
            }

            if (params.get("receiver_name") != null) {
                address.setName((String) params.get("receiver_name"));
            }
            if (params.get("receiver_phone") != null) {
                address.setPhone((String) params.get("receiver_phone"));
            }
            if (params.get("province") != null) {
                address.setProvince((String) params.get("province"));
            }
            if (params.get("city") != null) {
                address.setCity((String) params.get("city"));
            }
            if (params.get("district") != null) {
                address.setDistrict((String) params.get("district"));
            }
            if (params.get("detail_address") != null) {
                address.setDetail((String) params.get("detail_address"));
            }
            if (params.get("is_default") != null) {
                address.setIsDefault(Integer.valueOf(params.get("is_default").toString()));
            }
            address.setUpdatedAt(LocalDateTime.now());

            userAddressMapper.updateById(address);

            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", address);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete/{id}")
    public Map<String, Object> deleteAddress(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserAddress address = userAddressMapper.selectById(id);
            if (address == null) {
                result.put("code", 404);
                result.put("message", "地址不存在");
                return result;
            }
            address.setStatus(0);
            address.setUpdatedAt(LocalDateTime.now());
            userAddressMapper.updateById(address);

            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/set_default/{id}")
    public Map<String, Object> setDefaultAddress(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 先将该用户所有地址设为非默认
            List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
            for (UserAddress addr : addresses) {
                if (addr.getIsDefault() != null && addr.getIsDefault() == 1) {
                    addr.setIsDefault(0);
                    addr.setUpdatedAt(LocalDateTime.now());
                    userAddressMapper.updateById(addr);
                }
            }

            // 再将指定地址设为默认
            UserAddress address = userAddressMapper.selectById(id);
            if (address == null) {
                result.put("code", 404);
                result.put("message", "地址不存在");
                return result;
            }
            address.setIsDefault(1);
            address.setUpdatedAt(LocalDateTime.now());
            userAddressMapper.updateById(address);

            result.put("code", 200);
            result.put("message", "设置成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
