package com.zwj;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * @Description JAVAFX界面
 * @Author Zhou
 * @Date 2020/3/13
 * @Version 1.0
 */
public class Frame extends Application {

    private Parameter parameter = new Parameter();

    private Integer page = 1;   //默认第一页

    private Button backBtn = new Button("返回");

    private Label exerciseNumLabel = new Label("生成题目数：");
    private TextField exerciseNumField = new TextField();

    private Label scopeLabel = new Label("数值范围：");
    private TextField scopeField = new TextField();

    private Button directoryBtn = new Button("选择目录");
    private Label directoryLabel = new Label();

    private Button checkBtn = new Button("比对答案");
    private Button OKBtn = new Button("确定");

    private Label myAnswerLabel = new Label("我的答案：");
    private Label trueAnswerLabel = new Label("正确答案：");

    private Button myAnswerBtn = new Button("选择文件");
    private Button trueAnswerBtn = new Button("选择文件");

    private Label myAnswerFilePathLabel = new Label();
    private Label trueAnswerFilePathLabel = new Label();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage){

        primaryStage.setTitle("MyApp");
        primaryStage.setWidth(500);
        primaryStage.setHeight(300);

        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(exerciseNumLabel, 0, 1);
        grid.add(exerciseNumField,1,1);
        grid.add(scopeLabel,0,2);
        grid.add(scopeField,1,2);
        grid.add(directoryBtn,0,3);
        grid.add(directoryLabel,1,3);
        grid.add(checkBtn,1,4);
        grid.add(OKBtn,3,4);

        Group root = new Group();
        root.getChildren().add(grid);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        checkBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {

                page = 2;

                grid.getChildren().remove(exerciseNumLabel);
                grid.getChildren().remove(exerciseNumField);
                grid.getChildren().remove(scopeLabel);
                grid.getChildren().remove(scopeField);
                grid.getChildren().remove(checkBtn);
                grid.getChildren().remove(OKBtn);

                grid.add(myAnswerLabel,0,1);
                grid.add(myAnswerBtn,1,1);
                grid.add(myAnswerFilePathLabel,2,1);
                grid.add(trueAnswerLabel,0,2);
                grid.add(trueAnswerBtn,1,2);
                grid.add(trueAnswerFilePathLabel,2,2);
                grid.add(backBtn,1,4);
                grid.add(OKBtn,2,4);

                myAnswerBtn.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent arg0) {
                        FileChooser fileChooser = new FileChooser();
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("files (*.txt)", "*.txt");
                        fileChooser.getExtensionFilters().add(extFilter);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if(file != null){
                            myAnswerFilePathLabel.setText(file.getAbsolutePath());
                            parameter.setMyAnswersPath(file.getAbsolutePath());
                        }
                    }
                });
                trueAnswerBtn.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent arg0) {
                        FileChooser fileChooser = new FileChooser();
                        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("files (*.txt)", "*.txt");
                        fileChooser.getExtensionFilters().add(extFilter);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if(file != null){
                            trueAnswerFilePathLabel.setText(file.getAbsolutePath());
                            parameter.setTrueAnswersPath(file.getAbsolutePath());
                        }
                    }
                });
                backBtn.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent arg0) {
                        primaryStage.close();
                        Frame frame = new Frame();
                        frame.start(primaryStage);
                    }
                });
            }
        });

        directoryBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File file = directoryChooser.showDialog(primaryStage);
                if(file != null){
                    directoryLabel.setText(file.getAbsolutePath());
                    parameter.setDirectoryPath(file.getAbsolutePath());
                }
            }
        });


        OKBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {

                Stage stage = new Stage();
                stage.setTitle("查询结果");
                stage.setHeight(200);
                stage.setWidth(500);
                Label result = new Label();

                String exerciseNumStr = exerciseNumField.getText();
                String scopeStr = scopeField.getText();

                if((null == exerciseNumStr || "".equals(exerciseNumStr))
                    || (null == scopeStr || "".equals(scopeStr))){

                    if(page == 1){
                        result.setText("题目数、数值范围不能为空");
                    }else{

                        if(parameter.getMyAnswersPath() == null){
                            result.setText("我的答案文件不能为空");
                        }else if(parameter.getTrueAnswersPath() == null){
                            result.setText("正确答案文件不能为空");
                        }else if(parameter.getDirectoryPath() == null){
                            result.setText("成绩输出文件夹不能为空");
                        }else{

                            FileUtil fileUtil = new FileUtil();

                            String resultStr = fileUtil.checkAnswersProcess(parameter);

                            if(resultStr == null || "".equals(resultStr)){
                                result.setText("操作成功");
                            }else {
                                result.setText(resultStr);
                            }
                        }
                    }
                }else{
                    try{
                        parameter.setExerciseNum(Integer.valueOf(exerciseNumStr));
                        parameter.setScopeLength(Integer.valueOf(scopeStr));
                    }catch (Exception e){
                        result.setText("题目数、数值范围必须为整数");
                        System.out.println(e.getMessage());
                    }
                    if(directoryLabel.getText() == null){
                        result.setText("请选择目录");
                    }else{
                        MyUtil myUtil = new MyUtil();
                        parameter.setDirectoryPath(directoryLabel.getText());
                        try{
                            String error = myUtil.process(parameter);
                            if(error == null){
                                result.setText("操作成功");
                            }else{
                                result.setText(error);
                            }
                        }catch (Exception e){
                            result.setText("程序出错，错误信息：" + e.getMessage());
                        }
                    }
                }
                BorderPane pane = new BorderPane();
                pane.setCenter(result);
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();
            }
        });

    }
}
