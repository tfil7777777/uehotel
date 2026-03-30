package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.*;
import com.hotely5d.entity.*;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.RoomQuery;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CategoryService categoryService;

    public List<Appointment> findAll() {
        return appointmentMapper.selectList(null);
    }

    public Page<Appointment> search(Page<Appointment> page, Appointment appointment) {
        List<Appointment> list = appointmentMapper.selectPage(page, getEntityWrapper(appointment));
        for (Appointment a : list) {
            Room room = roomService.findById(a.getRoomId());
            a.setRoom(room);
            Member member = memberMapper.selectById(a.getMemberId());
            a.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Appointment> search(Appointment appointment) {
        return appointmentMapper.selectList(getEntityWrapper(appointment));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Appointment appointment) {
        EntityWrapper entityWrapper = new EntityWrapper();
        if (null != appointment) {
            if (!StringUtils.isEmpty(appointment.getRemark())) {
                entityWrapper.like("remark", String.valueOf(appointment.getRemark()));
            }
            if (!StringUtils.isEmpty(appointment.getMemberId())) {
                entityWrapper.eq("member_id", String.valueOf(appointment.getMemberId()));
            }
            if (!StringUtils.isEmpty(appointment.getStatus())) {
                entityWrapper.eq("status", String.valueOf(appointment.getStatus()));
            }
            entityWrapper.orderBy("id",false);
        }
        return entityWrapper;
    }

    public void modify(Appointment appointment) {
        appointmentMapper.updateById(appointment);
    }

    public Result add(Appointment appointment) {
        // 同一用户、同一房间若已有「待确认」预约，仅在与本次入住日期区间重叠时视为重复（与 canAppointment 按天占用规则一致）
        List<Appointment> pending = appointmentMapper.selectList(new EntityWrapper()
                .eq("member_id", appointment.getMemberId())
                .eq("room_id", appointment.getRoomId())
                .eq("status", 1));
        for (Appointment ex : pending) {
            if (stayRangesOverlap(ex.getStartTime(), ex.getDays(), appointment.getStartTime(), appointment.getDays())) {
                return new Result(false, StatusCode.ERROR, "请勿重复预订！");
            }
        }
        //判断是否已预约或者已入住这个房间
//        ArrayList<Integer> statusList = new ArrayList<>();
//        statusList.add(1);//已预订
//        statusList.add(2);//已入住
//        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
//                .eq("room_id", appointment.getRoomId())
//                .in("status", statusList));
//        if(ordersList.size() != 0){
//            return new Result(false, StatusCode.ERROR, "预订失败,房间已满！");
//        }
        //插入预约表
        Room r = roomService.findById(appointment.getRoomId());
        appointment.setMoney(r.getCategory().getPrice());
        appointment.setStatus(1);//1-待确认 2-预约成功 3-已取消
        appointmentMapper.insert(appointment);
        return new Result(true, StatusCode.OK, "操作成功,等待酒店确认！");
    }

    public Appointment findById(Integer id) {
        return appointmentMapper.selectById(id);
    }

    public void removeById(Integer id) {
        appointmentMapper.deleteById(id);
    }

    public List<Room> findRoomList(RoomQuery roomQuery) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("status",1);
        if(!StringUtils.isEmpty(roomQuery.getCategoryId())){
            wrapper.eq("category_id",roomQuery.getCategoryId());
        }
        List<Room> list = roomMapper.selectList(wrapper);
        for (Room r : list) {
            //查询房型
            Category category = categoryService.findById(r.getCategoryId());
            r.setCategory(category);
            //1.根据当前年月日和房间id判断是否入住，入住则不可预约和入住 2.根据当前年月日和房间id判断是否预约，预约成功则不可预约和入住
            ArrayList<Integer> statusList = new ArrayList<>();
            statusList.add(1);//已预订
            statusList.add(2);//已入住
            List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                    .eq("room_id", r.getId())
                    .in("status", statusList));
            if(ordersList.size() == 0){
                r.setCanUse(true);
                continue;
            }
            String startDate = DateFormatUtils.format(ordersList.get(0).getStartTime(),"yyyy-MM-dd");
            //不为0且当前日期!=入住日期，判断当前日期是否在入住日期+居住天数之后
            r.setCanUse(canAppointment(roomQuery.getStartDate(),startDate,ordersList.get(0).getDays()));
        }
        //过滤
        if(!StringUtils.isEmpty(roomQuery.getCanUse())){
            List<Room> rooms = list.stream().filter(f -> f.getCanUse() == roomQuery.getCanUse()).collect(Collectors.toList());
            return rooms;
        }
        return list;
    }

    /**
     * 入住占用日为 [start, start + days - 1]（与 findRoomList / canAppointment 一致）。两段有交集则不可再订。
     */
    private boolean stayRangesOverlap(Date startA, Integer daysA, Date startB, Integer daysB) {
        if (startA == null || startB == null || daysA == null || daysB == null || daysA < 1 || daysB < 1) {
            return true;
        }
        try {
            Date a0 = DateUtils.truncate(startA, Calendar.DAY_OF_MONTH);
            Date a1 = DateUtils.addDays(a0, daysA - 1);
            Date b0 = DateUtils.truncate(startB, Calendar.DAY_OF_MONTH);
            Date b1 = DateUtils.addDays(b0, daysB - 1);
            return !(a1.before(b0) || b1.before(a0));
        } catch (Exception e) {
            return true;
        }
    }

    //判断是否可预约
    public boolean canAppointment(String currentDate,String startDate,Integer days)  {
        List<String> dateList = new ArrayList();

        for (int i = 0; i < days ; i++) {
            try {
                Date date = DateUtils.parseDate(startDate, "yyyy-MM-dd");
                Date rs = DateUtils.addDays(date, i);
                dateList.add(DateFormatUtils.format(rs,"yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return !dateList.contains(currentDate);
    }

    public Result confirm(Integer appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        //判断是否有入住但是没退房或者有预订但是没入住
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add(1);//已预订
        statusList.add(2);//已入住
        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                .eq("room_id", appointment.getRoomId())
                .in("status", statusList));
        if(ordersList.size() > 0){
            return new Result(false, StatusCode.ERROR,"该房间已被占用，请取消预约或者前往【入住管理】退房！");
        }
        //修改预订状态，预订成功
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
        //生成入住订单，状态为已预订
        Orders order = new Orders();
        order.setMemberId(appointment.getMemberId());
        order.setRoomId(appointment.getRoomId());
        order.setDays(appointment.getDays());
        order.setStartTime(appointment.getStartTime());
        order.setMoney(appointment.getMoney());
        order.setStatus(1);
        ordersMapper.insert(order);
        return new Result(true, StatusCode.OK,"操作成功！");
    }
}
