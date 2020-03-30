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
import javafx.stage.Stage;

import java.io.File;

/**
 * @Description JAVAFX界面
 * @Author Zhou
 * @Date 2020/3/13
 * @Version 1.0
 */
public class Frame extends Application {

    private Label exerciseNumLabel = new Label("生成题目数：");
    private TextField exerciseNumField = new TextField();

    private Label scopeLabel = new Label("数值范围：");
    private TextField scopeField = new TextField();

    private Button directoryBtn = new Button("选择目录");
    private Label directoryLabel = new Label();

    private Button OKBtn = new Button("确定");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        primaryStage.setTitle("MyApp");
        primaryStage.setWidth(500);
        primaryStage.setHeight(300);

        GridPane grid = new GridPane();
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
        grid.add(OKBtn,3,4);

        Group root = new Group();
        root.getChildren().add(grid);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        directoryBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                DirectoryChooser directoryChooser=new DirectoryChooser();
                File file = directoryChooser.showDialog(primaryStage);
                if(file != null){
                    directoryLabel.setText(file.getAbsolutePath());
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

                    result.setText("题目数、数值范围不能为空");
                }else{

                    Parameter parameter = new Parameter();
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
