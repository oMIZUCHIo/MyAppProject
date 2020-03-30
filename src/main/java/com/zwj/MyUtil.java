package com.zwj;

import java.util.*;

/**
 * @Description 生成题目工具类
 * @Author Zhou
 * @Date 2020/3/21
 * @Version 1.0
 */
public class MyUtil {

    /**
     * @description 类入门
     */
    public String process(Parameter parameter){

        FileUtil fileUtil = new FileUtil();

        String judgeResult = fileUtil.paramJudge(parameter);
        if(judgeResult != null) //不为空则说明有出错
            return judgeResult;

        StringBuilder problemStr = new StringBuilder();

        StringBuilder resultStr = new StringBuilder();

        for(int i = 1 ; i <= parameter.getExerciseNum() ; i ++){
            //问题
            String problem = createProblem(parameter);

            //计算结果
            String result = countProblem(problem);
            if("error".equals(result)){ //出现负号，跳过此题
                i --;
            }else{
                problemStr.append(i).append(". ").append(problem).append(" =\n\n");
                //化简结果
                String simplyResult = simplyResult(result);

                resultStr.append(i).append(". ").append(simplyResult).append("\n\n");
            }
        }
        return fileUtil.wirte(problemStr.toString(),resultStr.toString(),parameter.getDirectoryPath());
    }

    private String createProblem(Parameter parameter){

        Random random = new Random();

        char[] operatorCharList = new char[]{'+', '-', '*', '÷' , '('};

        int operatorCharScope = 5;  //可使用操作符范围

        String operatorNum;     //操作数

        boolean useLeftBrackets = false;  //是否使用左括号

        int numInBrackets = 0;      //括号包围数字数目

        //生成的式子
        StringBuilder line = new StringBuilder();

        //操作符数目设置为 1-3 个
        int operatorCharNum = 1 + random.nextInt(2);

        for(int i = 0 ; i < operatorCharNum ; i ++){

            //生成操作数
            operatorNum = createOperatorNum(parameter.getScopeLength());

            //生成操作符
            char operatorChar = operatorCharList[random.nextInt(operatorCharScope)];

            if(operatorChar == '('){

                //如果还未使用操作符，则 加入左括号 并 生成新的不为括号的操作符
                if(! useLeftBrackets){

                    operatorChar = operatorCharList[random.nextInt(operatorCharScope - 1)];

                    useLeftBrackets = true; //已使用过左括号

                    numInBrackets ++;

                    line.append('(').append(' ').append(operatorNum).append(' ').append(operatorChar).append(' ');

                    //当已使用左括号 且 括号中数字数目为0时 需更换此时操作符为 新的不为括号的操作符
                }else if(numInBrackets == 0){

                    operatorChar = operatorCharList[random.nextInt(operatorCharScope - 1)];

                    line.append(operatorNum).append(' ').append(operatorChar).append(' ');

                    //当已使用左括号 且 括号中数字数目大于1时
                }else {

                    operatorChar = operatorCharList[random.nextInt(operatorCharScope - 1)];

                    line.append(operatorNum).append(' ').append(')').append(' ').append(operatorChar).append(' ');

                    operatorCharScope = 4;    //可使用操作符范围设为4，之后不会再生成 括号字符
                }
            }
            //操作符不是括号就直接添加对应的式子就行
            line.append(operatorNum).append(' ').append(operatorChar).append(' ');
        }
        operatorNum = createOperatorNum(parameter.getScopeLength());
        line.append(operatorNum);

        //如果使用了左括号 且 可使用操作符范围为 0-4 则说明未使用过 右括号  对左括号进行匹配，需要手动添加
        if(useLeftBrackets && operatorCharScope == 5){

            line.append(' ').append(')');
        }

        String lineStr = line.toString();

        if(lineStr.startsWith("(") && lineStr.endsWith(")")){

            lineStr = lineStr.substring(1 , lineStr.length() - 1);
        }
        return lineStr;
    }

    /**
     * @description 计算结果
     * @param problem 问题
     * @return java.lang.String
     */
    String countProblem(String problem){

        List<String> charList = converProblemToList(problem);

        //含有括号，计算括号内容结果，再用结果替换括号
        if(charList.indexOf("(") >= 0){

            int i = charList.indexOf("(");

            int temp = i;

            StringBuilder sb = new StringBuilder();
            i ++;
            while (!charList.get(i).equals(")")){
                sb.append(charList.get(i)).append(" "); //得出括号内的计算式
                charList.remove(i); //删去括号内内容
            }
            charList.remove(i); //删去多余的右括号
            String bracketResult = countProblem(sb.toString()); //计算括号内式子
            if(bracketResult.equals("error")){
                return bracketResult;     //出现负数
            }else if(bracketResult.equals("X")){ //除数出现0
                return bracketResult;
            }
            charList.set(temp,bracketResult);   //用结果替换原来的左括号内容
        }
            //此时已不含括号 , 格式： 1 + 2 * 3 + 8
            //先进行乘除的优先计算
            for(int i = 1 ; i < charList.size() ; ){

                if(charList.get(i).equals("*") || charList.get(i).equals("÷")){
                    String num1 = charList.get(i - 1);
                    String num2 = charList.get(i + 1);
                    String countResult = count(num1,num2,charList.get(i));

                    if(countResult == null){
                        return "X";     //除数出现0
                    }
                    System.out.println("num1 " + num1 + " " + charList.get(i) + " " + "num2 " + num2 + " = " + countResult);

                    charList.set(i - 1,countResult);
                    charList.remove(i + 1);
                    charList.remove(i);

                    System.out.println("size：" + charList.size() + "；" + charList.toString());
                }else{
                    i = i + 2;  //不为乘除号则查询下一个操作符
                }
            }
            //集合大小不为1则说明还未计算完成，此时式中只剩加减号
            if(charList.size() != 1){
                for(int i = 1 ; i < charList.size() ; ){
                    String num1 = charList.get(i - 1);
                    String num2 = charList.get(i + 1);
                    String countResult = count(num1,num2,charList.get(i));
                    if(countResult == null){
                        return "error";     //出现负数
                    }
                    charList.set(i - 1,countResult);
                    charList.remove(i + 1);
                    charList.remove(i);
                }
            }
        return charList.get(0);
    }

    /**
     * @description 生成操作数字
     * @param range 取值范围
     */
    String createOperatorNum(int range){

        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        //生成整数或分数的随机标识，0表示生成整型，1表示生成分数
        int flag = random.nextInt(2);

        if(flag == 0){

            sb.append(random.nextInt(range + 1));
        }else {
            int denominator = 1 + random.nextInt(range); //分母
            int numerator = 1 + random.nextInt(range);   //分子

            //当分子大于分母时
            if(numerator > denominator){

                int intNum = numerator / denominator;   //假分数前的整数
                numerator = numerator % denominator;   //得出新的分子

                //整除
                if(numerator == 0){
                    sb.append(intNum);
                }else{
                    sb.append(intNum).append('\'').append(numerator).append("/").append(denominator);
                }
            }else if(numerator == denominator){

                sb.append(1);
            }else if(denominator == 1){

                sb.append(numerator);
            }else{

                sb.append(numerator).append("/").append(denominator);
            }
        }
        return sb.toString();
    }

    /**
     * @description 将问题中的各字符放入集合，便于后续操作
     */
    private List<String> converProblemToList(String problem){

        problem = problem.trim();

        String[] chars = problem.split(" ");

        return new ArrayList<>(Arrays.asList(chars));
    }

    /**
     * @description 判断字符是否为操作符还是数字
     * @param str
     * @return boolean
     */
    private boolean checkIfOperator(String str){

        switch (str){
            case "+" :
            case "-" :
            case "*" :
            case "÷" : return true;
            default : return false;
        }
    }

    /**
     * @description 两数计算
     * @param num1  数1
     * @param num2  数2
     * @param operatorChar 操作符
     * @return java.lang.String
     */
    String count(String num1, String num2, String operatorChar){

        String result;

        int[] result1 = converNum(num1);
        int[] result2 = converNum(num2);

        int numerator1 = result1[0];
        int denominator1 = result1[1];
        int numerator2 = result2[0];
        int denominator2 = result2[1];

        System.out.println(numerator1 + "/" + denominator1 + "  " + numerator2 + "/" + denominator2);

        switch (operatorChar){
            case "+" :
                result = add(numerator1,denominator1,numerator2,denominator2);break;
            case "-" :
                result = sub(numerator1,denominator1,numerator2,denominator2);break;
            case "*" :
                result = muti(numerator1,denominator1,numerator2,denominator2);break;
            case "÷" :
                result = div(numerator1,denominator1,numerator2,denominator2);break;
            default :
                result = null;
        }
        return result;
    }

    /**
     * @description 加法
     * @return java.lang.String
     */
    private String add(int numerator1, int denominator1, int numerator2, int denominator2) {

        StringBuilder sb = new StringBuilder();

        //都为整型时
        if(denominator1 == 1 && denominator2 == 1){

            sb.append(numerator1 + numerator2);

            //两者中出现分数时
        }else{

            numerator1 = denominator2 * numerator1;

            numerator2 = denominator1 * numerator2;

            int resultNumerator = numerator1 + numerator2;
            int resultDenominator = denominator2 * denominator1;

            sb.append(resultNumerator).append("/").append(resultDenominator);
        }
        return sb.toString();
    }

    /**
     * @description 减法
     * @return java.lang.String
     */
    private String sub(int numerator1, int denominator1, int numerator2, int denominator2) {

        StringBuilder sb = new StringBuilder();

        //都为整型时
        if(denominator1 == 1 && denominator2 == 1){

            if(numerator1 > numerator2){
                sb.append(numerator1 - numerator2);
            }else{
                return null;    //出现负数
            }
            //两者中出现分数时
        }else{

            numerator1 = denominator2 * numerator1;

            numerator2 = denominator1 * numerator2;

            int resultNumerator;
            if(numerator1 > numerator2){
                resultNumerator = numerator1 - numerator2;
            }else{
                return null;    //出现负数
            }
            int resultDenominator = denominator2 * denominator1;

            sb.append(resultNumerator).append("/").append(resultDenominator);
        }
        return sb.toString();
    }

    /**
     * @description 乘法
     * @return java.lang.String
     */
    private String muti(int numerator1, int denominator1, int numerator2, int denominator2) {

        StringBuilder sb = new StringBuilder();

        //都为整型时
        if(denominator1 == 1 && denominator2 == 1){

            sb.append(numerator1 * numerator2);
            //两者中出现分数时
        }else{

            int resultNumerator = numerator1 * numerator2;

            int resultDenominator = denominator2 * denominator1;

            sb.append(resultNumerator).append("/").append(resultDenominator);
        }
        return sb.toString();
    }

    /**
     * @description 除法
     * @return java.lang.String
     */
    private String div(int numerator1, int denominator1, int numerator2, int denominator2) {

        StringBuilder sb = new StringBuilder();

        //都为整型时
        if(denominator1 == 1 && denominator2 == 1){

            if(numerator2 == 0){
                return null;    //返回null，表示除数不能为0
            }else{
                sb.append(numerator1).append("/").append(numerator2);
            }
            //两者中出现分数时
        }else{

            int resultNumerator = numerator1 * denominator2;

            int resultDenominator = denominator1 * numerator2;

            if(resultDenominator == 0){
                return null;
            }else{
                sb.append(resultNumerator).append("/").append(resultDenominator);
            }
        }
        return sb.toString();
    }


    /**
     * @description 若操作数为真分数返回分子，分母，整型分母为1
     * @param num 操作数
     * @return int[]
     */
    int[] converNum(String num){

        int denominator;    //分母
        int numerator;      //分子

        String[] strings1 = num.split("'");

        //为带分数
        if(strings1.length > 1){

            int frontNum = Integer.valueOf(strings1[0]);

            denominator = Integer.valueOf(strings1[1].split("/")[1]); //分母

            numerator = Integer.valueOf(strings1[1].split("/")[0]);   //分子

            numerator = frontNum * denominator + numerator;    //将假分数化为真分数

        }else{

            String[] strings2 = strings1[0].split("/");

            //为分数
            if(strings2.length > 1){

                numerator = Integer.valueOf(strings2[0]);

                denominator = Integer.valueOf(strings2[1]);

                //为整型
            }else {

                numerator = Integer.valueOf(num);

                denominator = 1;
            }
        }
        return new int[]{numerator,denominator};
    }

    /**
     * @description 化简计算结果
     * @param result 结果
     * @return java.lang.String
     */
    String simplyResult(String result){

        if(result.equals("X")){
            return result;  //特殊情况直接返回
        }
        String[] splitResult = result.split("/"); //因为结果必为真分数/或整型，所以直接分割

        //本身结果就是整型
        if(splitResult.length == 1){
            return result;  //不做处理
        }else {

            int numerator = Integer.valueOf(splitResult[0]);    //分子
            int denominator = Integer.valueOf(splitResult[1]);  //分母

            //若分母为1，直接返回分子
            if(denominator == 1){
                return String.valueOf(numerator);
                //若分子为0，直接返回0
            }else if(numerator == 0){
                return "0";
            }else{
                //分母整除分子
                if(denominator % numerator == 0){

                    return 1 + "/" + denominator / numerator;
                }else{

                    int intNum = numerator / denominator;   //假分数前的整数
                    numerator = numerator % denominator;   //得出新的分子
                    //余数为0 整除
                    if(numerator == 0){
                        //返回整除结果
                        return String.valueOf(intNum);
                    }else{
                        if(intNum == 0){

                            return simplyDivision(numerator,denominator);
                        }else{
                            if(denominator % numerator == 0){
                                return intNum + "'" + "1/" + denominator / numerator;
                            }else{
                                return intNum + "'" + simplyDivision(numerator,denominator);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @description 化简真分数
     * @param numerator 分子
     * @param denominator 分母
     * @return java.lang.String
     */
    private String simplyDivision(int numerator, int denominator){

        int y = 1;
        for (int i = numerator ; i >= 1; i--) {
            if (numerator % i == 0 && denominator % i == 0) {
                y = i;
                break;
            }
        }
        int z = numerator / y;// 分子
        int m = denominator / y;// 分母
        if (z == 0) {
            return "0";
        }
        if(m == 1){
            return String.valueOf(z);
        }
        return z + "/" + m;
    }

}
