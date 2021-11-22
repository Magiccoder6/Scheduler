package com.sh.sheduler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label ProcessNum;

    @FXML
    private Slider ProcessSelector;

    @FXML
    private ComboBox<Integer> NumberSelector;

    @FXML
    private Label Numbers;

    @FXML
    private Label Elements;

    @FXML
    private ListView<?> ProcessList;

    @FXML
    private Label StartingPosition;


    Map<String,Integer> elems;
    private int NumOfProcess;
    private int StartPosition=1;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //add 10 numbers to combo box
        for(int i=1;i<11;i++){
            NumberSelector.getItems().add(i);
        }

        elems=generateNumbers();//save elements
        Numbers.setText(elems.values().toString());//set elements to label
        Elements.setText(elems.toString());//set integers to label


    }

    //generate integer elements
    private Map<String,Integer> generateNumbers(){
        Map<String,Integer> temp = new HashMap<>();

        for(int i=1;i<11;i++){
            int nums = (int) ((Math.random() * (99 - 1)) + 1);
            temp.put("EL"+ i,nums);

        }

        return temp;
    }


    @FXML
    void updateProcess(MouseEvent event) {
        NumOfProcess = (int) ProcessSelector.getValue();
        ProcessNum.setText(Integer.toString(NumOfProcess));

    }

    @FXML
    void updatePosition(ActionEvent event) {
        int position = NumberSelector.getValue();

        //Set starting position on label
        StartingPosition.setText(String.valueOf(position));

        //store position in variable
        StartPosition=position;
    }
}