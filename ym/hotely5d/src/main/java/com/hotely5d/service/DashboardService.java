package com.hotely5d.service;

import com.hotely5d.dao.MemberMapper;
import com.hotely5d.dao.OrdersMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private MemberMapper memberMapper;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        // 核心指标
        result.put("totalOrders", ordersMapper.countTotalOrders());
        result.put("totalPrice", ordersMapper.countTotalPrice());

        // 图表数据
        result.put("ordersByCategory", ordersMapper.countOrdersByCategory());
        result.put("dailyOrders", ordersMapper.countDailyOrders());
        result.put("dailyMembers", memberMapper.countDailyMembers());

        return result;
    }
}