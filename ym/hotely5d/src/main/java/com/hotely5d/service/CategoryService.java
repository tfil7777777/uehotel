package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.CategoryMapper;
import com.hotely5d.dao.RoomMapper;
import com.hotely5d.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RoomMapper roomMapper;

    public List<Category> findAll() {
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", category.getId()).eq("status", 1));
            category.setRoomNum(count);
        }
        return categories;
    }

    public Page<Category> search(Page<Category> page,Category category) {
        List<Category> list = categoryMapper.selectPage(page, getEntityWrapper(category));
        for (Category c : list) {
            Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", c.getId()).eq("status", 1));
            c.setRoomNum(count);
        }
        return page.setRecords(list);
    }

    public List<Category> search(Category category) {
        List<Category> list = categoryMapper.selectList(getEntityWrapper(category));
        for (Category c : list) {
            Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", c.getId()).eq("status", 1));
            c.setRoomNum(count);
        }
        return list;
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Category category){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != category){
            if(!StringUtils.isEmpty(category.getCategoryName())){
                entityWrapper.like("category_name",String.valueOf(category.getCategoryName()));
            }
            if(!StringUtils.isEmpty(category.getPhoto())){
                entityWrapper.like("photo",String.valueOf(category.getPhoto()));
            }
            if(!StringUtils.isEmpty(category.getArea())){
                entityWrapper.like("area",String.valueOf(category.getArea()));
            }
            if(!StringUtils.isEmpty(category.getIntroduce())){
                entityWrapper.like("introduce",String.valueOf(category.getIntroduce()));
            }
        }
        return entityWrapper;
    }

    public void modify(Category category) {
        categoryMapper.updateById(category);
    }

    public void add(Category category) {
        categoryMapper.insert(category);
    }

    public Category findById(Integer id) {
        Category c = categoryMapper.selectById(id);
        Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", c.getId()).eq("status", 1));
        c.setRoomNum(count);
        return c;
    }

    public void removeById(Integer id) {
        categoryMapper.deleteById(id);
    }
}
