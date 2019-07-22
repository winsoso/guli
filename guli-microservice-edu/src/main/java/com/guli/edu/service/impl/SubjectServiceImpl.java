package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guli.common.util.ExcelImportUtil;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.SubjectNestedVo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author winsoso
 * @since 2019-07-13
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public ArrayList<Object> batchImport(MultipartFile file) throws Exception {
        //创建错误提示列表
        ArrayList<Object> errorMsg = new ArrayList<>();
        ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(file.getInputStream());

        HSSFSheet sheet = excelHSSFUtil.getSheet();

        int rowCount = sheet.getPhysicalNumberOfRows();
        if(rowCount<=1){
            errorMsg.add("表格中数据不存在,请你填写数据");
            return errorMsg;
        }
        //循环读取
        for (int rowNum = 1; rowNum <rowCount; rowNum ++){
            HSSFRow rowData = sheet.getRow(rowNum);
            if(rowData != null){


            //读取一级分类（index= 0 的列）
            HSSFCell leaveOneCell = rowData.getCell(0);

            String leaveOneCellValue ="";
            if(leaveOneCell !=null){

                leaveOneCellValue = excelHSSFUtil.getCellValue(leaveOneCell).trim();
                if(StringUtils.isEmpty(leaveOneCellValue)){
                   errorMsg.add("第"+rowNum+"行记录一级类别为空，请填写完整");
                  // return errorMsg;
                   continue;
                }
            }
            //判断一级分类是否重复

                Subject subject = this.getByTitle(leaveOneCellValue);
            String parentId = null;
                if (subject == null){
                    //不重复
                    //将一级分类插入到数据库
                    Subject subjectLevelOne = new Subject();
                    subjectLevelOne.setParentId("0");
                    subjectLevelOne.setTitle(leaveOneCellValue);
                    subjectLevelOne.setSort(rowNum);
                    baseMapper.insert(subjectLevelOne);
                     parentId = subjectLevelOne.getId();

                }else{
                    //重复
                    parentId = subject.getId();
                }
                //获取二级分类
                String levelTwoValue = "";
                Cell levelTwoCell = rowData.getCell(1);
                if(levelTwoCell != null){
                    levelTwoValue = excelHSSFUtil.getCellValue(levelTwoCell).trim();
                    if (StringUtils.isEmpty(levelTwoValue)) {
                        errorMsg.add("第" + rowNum + "行二级分类为空");
                        continue;
                    }
                }
                    //判断二级分类是否重复
                    Subject subjectSub = this.getSubByTitle(levelTwoValue, parentId);
                    Subject subjectLevelTwo = null;
                    if(subjectSub == null){
                    //将二级分类存入数据库
                    subjectLevelTwo = new Subject();
                    subjectLevelTwo.setTitle(levelTwoValue);
                    //将一级分类获取到的ID 作为二级分类的parentId存储
                    subjectLevelTwo.setParentId(parentId);
                    subjectLevelTwo.setSort(rowNum);
                    baseMapper.insert(subjectLevelTwo);//添加
                }else{
                        errorMsg.add("第" + rowNum + "行二级分类数据重复");
                        continue;
                    }
               }
         }


        return errorMsg;


    }

    @Override
    public List<SubjectNestedVo> nestedList() {

        //最终要返回的数据列表
        ArrayList<SubjectNestedVo> subjectNestedVosArrayList = new ArrayList<>();

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);

        queryWrapper.orderByAsc("sort","id");
        List<Subject> subjects = baseMapper.selectList(queryWrapper);

        for (int i = 0; i <subjects.size() ; i++) {
            Subject subject = subjects.get(i);
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();

            BeanUtils.copyProperties(subject, subjectNestedVo);

            subjectNestedVosArrayList.add(subjectNestedVo);
        }
        return subjectNestedVosArrayList;




    }

    /**
     * 根据分类名称和父id查询这个一级分类中否存在
     * @param title
     * @return
     */
    private Subject getByTitle(String title) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", "0");//一级分类
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类中否存在
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
