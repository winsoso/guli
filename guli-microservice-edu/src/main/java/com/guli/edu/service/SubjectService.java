package com.guli.edu.service;

import com.guli.edu.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.vo.SubjectNestedVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author winsoso
 * @since 2019-07-13
 */
public interface SubjectService extends IService<Subject> {
    ArrayList<Object> batchImport (MultipartFile file) throws Exception;

    List<SubjectNestedVo> nestedList();

}
