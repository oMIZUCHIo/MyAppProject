package com.zwj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

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
    String paramJudge(Parameter parameter) {

        File file = new File(parameter.getDirectoryPath());
        if (!file.exists() || !file.isDirectory()) {
            return "所选不存在 或 所选的不是文件夹";
        } else
            return null;
    }

    /**
     * @param problemStr    问题
     * @param resultStr     答案
     * @param directoryPath 当前文件夹路径
     * @return java.lang.String
     * @description 将信息写入文件中
     */
    String wirte(String problemStr, String resultStr, String directoryPath) {
        try {
            File problemFile = new File(directoryPath + "\\Exercises.txt");
            boolean flag1 = problemFile.createNewFile();
            File resultFile = new File(directoryPath + "\\Answers.txt");
            boolean flag2 = resultFile.createNewFile();
            if (!(flag1 && flag2)) {
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

    /**
     * @description 比对答案
     * @param trueAnswersPath  用户答案路径
     * @param myAnswersPath 正确答案路径
     * @param directoryPath 成绩输出文件夹
     * @return java.lang.String
     */
    String checkAnswers(String trueAnswersPath, String myAnswersPath, String directoryPath) {

        try {
            BufferedReader myreader = new BufferedReader(new FileReader(myAnswersPath));
            BufferedReader truereader = new BufferedReader(new FileReader(trueAnswersPath));

            StringBuilder trueResult = new StringBuilder();
            StringBuilder falseResult = new StringBuilder();

            int trueNum = 0;
            int falseNum = 0;

            String[] myStrings;
            String[] trueStrings;

            String myLine = null;    //用户答案逐行
            String trueLine = null; //正确答案逐行

            String myAnswers = null;    //用户答案
            String trueAnswers = null;  //正确答案

            int myIndex = 0;
            int trueIndex = 0;

            while ((myLine = myreader.readLine()) != null) {

                if (myLine.trim().equals("")) {     //跳过空行
                    continue;
                }
                trueLine = truereader.readLine();
                while(trueLine.trim().equals("")){  //跳过空行
                    trueLine = truereader.readLine();
                }
                myStrings = myLine.split(". ");
                trueStrings = trueLine.split(". ");

                myIndex = Integer.valueOf(myStrings[0].trim()); //题目序号
                trueIndex = Integer.valueOf(trueStrings[0].trim());

                while(myIndex > trueIndex){     //使答案序号一致
                    trueLine = truereader.readLine();
                    while(trueLine.trim().equals("")){  //跳过空行
                        trueLine = truereader.readLine();
                    }
                    trueStrings = trueLine.split(". ");
                    trueIndex = Integer.valueOf(trueStrings[0].trim());
                }
                while(myIndex < trueIndex){     //使答案序号一致
                    myLine = myreader.readLine();
                    while(myLine.trim().equals("")){  //跳过空行
                        myLine = myreader.readLine();
                    }
                    myStrings = myLine.split(". ");
                    myIndex = Integer.valueOf(myStrings[0].trim());
                }
                if (myStrings.length == 1) {    //无答案直接算错
                    falseResult.append(myIndex).append(",");
                    falseNum ++;
                    continue;
                }
                myAnswers = myLine.split(". ")[1].trim();        //用户答案
                trueAnswers = trueLine.split(". ")[1].trim();    //正确答案
                if(myAnswers.equals(trueAnswers)){
                    trueResult.append(myIndex).append(",");
                    trueNum ++;
                }else{
                    falseResult.append(myIndex).append(",");
                    falseNum ++;
                }
            }
            myreader.close();

            String trueStr = trueResult.toString();
            String falseStr = falseResult.toString();

            File outPutFile = new File(directoryPath + "\\Grade.txt");
            int i = 1;
            while(outPutFile.exists()){ //防止文件名重复造成的文件创建失败
                outPutFile = new File(directoryPath + "\\Grade(" + i + ").txt");
                i ++;
            }
            boolean flag = outPutFile.createNewFile();
            if (!flag) {
                return "创建成绩输出文件出错";
            }
            FileOutputStream fos = new FileOutputStream(outPutFile.getAbsolutePath());

            String grade = "Correct: " + trueNum + "(" + trueStr.substring(0,trueStr.length() - 1) + ")\n\n"
                    + "Wrong: " + falseNum + "(" + falseStr.substring(0,falseStr.length() - 1) + ")";

            fos.write(grade.getBytes());
            fos.close();
        } catch (Exception e) {
            return "答案格式出错！，每道题格式如：1. 1/2（序号.空格+答案），" + e.getMessage();
        }
        return null;
    }

    /**
     * @description 输出成绩方法入口
     */
    String checkAnswersProcess(Parameter parameter){

        File file = new File(parameter.getMyAnswersPath());

        if(!file.exists() || file.isDirectory()){
            return "所选用户答案文件不存在 或 所选的用户答案文件是文件夹";
        }

        file = new File(parameter.getTrueAnswersPath());
        if(!file.exists() || file.isDirectory()){
            return "所选正确答案文件不存在 或 所选的正确答案文件是文件夹";
        }

        file = new File(parameter.getDirectoryPath());
        if (!file.exists() || !file.isDirectory()) {
            return "所选成绩输出文件夹不存在 或 所选的成绩输出文件夹不是文件夹";
        }
        return checkAnswers(parameter.getTrueAnswersPath(),parameter.getMyAnswersPath(),parameter.getDirectoryPath());
    }
}
