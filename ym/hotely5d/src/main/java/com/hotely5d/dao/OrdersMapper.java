package com.hotely5d.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.hotely5d.entity.Orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface OrdersMapper extends BaseMapper<Orders>{

    // 1. 统计总订单数
    @Select("SELECT COUNT(*) FROM `orders` ")
    Integer countTotalOrders();

    // 2. 统计总销售额 (假设 status=1 代表已支付/已完成)
    @Select("SELECT SUM(price) FROM `orders` WHERE status = 1")
    BigDecimal countTotalPrice();

    // 3. 统计各房间类型订单数 (用于饼图)
    @Select("SELECT r.name, COUNT(o.id) AS value FROM `orders` o JOIN `room` r ON o.room_id = r.id GROUP BY r.name")
    List<Map<String, Object>> countOrdersByCategory();

    // 4. 统计近7日每日订单量 (用于柱状图)
    @Select("SELECT DATE(create_time) AS date, COUNT(id) AS count FROM `orders` WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> countDailyOrders();

}
