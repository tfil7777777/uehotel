package com.hotely5d.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.hotely5d.entity.Member;

import java.util.List;
import java.util.Map;

@Repository
public interface MemberMapper extends BaseMapper<Member>{

    // 统计近7日每日用户增长数 (用于折线图)
    @Select("SELECT DATE(create_time) AS date, COUNT(id) AS count FROM `member` WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> countDailyMembers();

}
