package com.zwj;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Description 文件工具类
 * @Author Zhou
 * @Date 2020/3/29
 * @Version 1.0
 */
public class FileUtil {


     /**
      * @description 判断所选文件夹的合法性
      */
     String paramJudge(Parameter parameter){

        File file = new File(parameter.getDirectoryPath());
        if(!file.exists() || !file.isDirectory()){
            return "所选不存在 或 所选的不是文件夹";
        }else
            return null;
    }

    /**
     * @description 将信息写入文件中
     * @param problemStr 问题
     * @param resultStr 答案
     * @param directoryPath 当前文件夹路径
     * @return java.lang.String
     */
    String wirte(String problemStr, String resultStr, String directoryPath){
        try {
            File problemFile = new File(directoryPath + "\\Exercises.txt");
            boolean flag1 = problemFile.createNewFile();
            File resultFile = new File(directoryPath + "\\Answers.txt");
            boolean flag2 = resultFile.createNewFile();
            if(!(flag1 && flag2)){
                return "创建文件出错";
            }
            FileOutputStream fos = new FileOutputStream(problemFile.getAbsolutePath());
            fos.write(problemStr.getBytes());

            fos.flush();

            fos = new FileOutputStream(resultFile.getAbsolutePath());
            fos.write(resultStr.getBytes());

            fos.close();
            return null;
        } catch (Exception e) {
            return "文件写入出错，错误原因：" + e.getMessage();
        }
    }
}
