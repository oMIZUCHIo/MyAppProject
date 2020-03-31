package com.zwj;

import lombok.Data;

/**
 * @Description 入参封装
 * @Author Zhou
 * @Date 2020/3/21
 * @Version 1.0
 */
@Data
class Parameter {

    //题目数
    private Integer exerciseNum;

    //取值范围
    private Integer scopeLength;

    //选择的文件夹路径
    private String directoryPath;

    //正确答案路径
    private String trueAnswersPath;

    //用户答案路径
    private String myAnswersPath;
}
