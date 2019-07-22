package com.guli.edu.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: win soso
 * @Date: 2019/7/23 01:57
 * @Description:
 */
public class SubjectNestedVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private List<SubjectVo> children = new ArrayList<>();
}