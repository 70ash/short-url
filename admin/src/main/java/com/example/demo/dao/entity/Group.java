package com.example.demo.dao.entity;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author 70ash
 * Date 2024/2/20 16:37
 * Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group extends BaseDO implements Serializable{

    private static final long serialVersionUID = 1L;

    // @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;


}