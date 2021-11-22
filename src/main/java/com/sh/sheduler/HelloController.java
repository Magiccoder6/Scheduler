package com.sh.sheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

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
    private ListView<OSProcess> ProcessList;

    @FXML
    private Label StartingPosition;


    private Map<String,Integer> elems;//list of integer elements
    private int NumOfProcess;//Number of processes
    private int StartPosition=1;//starting position to access elements
    private Queue<OSProcess> processes;//stores all processes in a queue
    private ObservableList<OSProcess> ActiveProcesses = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //add 10 numbers to combo box
        for(int i=1;i<11;i++){
            NumberSelector.getItems().add(i);
        }

        elems=generateNumbers();//save elements
        Numbers.setText(elems.values().toString());//set elements to label
        Elements.setText(elems.toString());//set integers to label

        saveProcesses();//save processes to data structure

    }

    private void saveProcesses(){
        processes= new LinkedList<>();
        int  num = (int) ProcessSelector.getValue();//get the number of processes

       for(int i=0;i<num;i++){
            processes.add(new OSProcess("P"+(i+1)));//add all processes to a queue

        }

        for(int i=0;i<5;i++){
           ActiveProcesses.add(processes.poll());//add 5 processes to active process list
        }

        //get the name property from OSProcess custom property
        ProcessList.setCellFactory(param -> new ListCell<OSProcess>() {
            @Override
            protected void updateItem(OSProcess item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.Name == null) {
                    setText(null);
                    setGraphic(null);

                } else {
                    setText(item.Name);
                }
            }
        });
        //
        ProcessList.setItems(ActiveProcesses);
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

    //called when the user select number of processes
    @FXML
    void updateProcess(MouseEvent event) {
        NumOfProcess = (int) ProcessSelector.getValue();
        ProcessNum.setText(Integer.toString(NumOfProcess));
        ActiveProcesses.removeAll();
        ProcessList.getItems().clear();
        saveProcesses();
    }

    //combo box to select the starting position
    @FXML
    void updatePosition(ActionEvent event) {
        int position = NumberSelector.getValue();

        //Set starting position on label
        StartingPosition.setText(String.valueOf(position));

        //store position in variable
        StartPosition=position;
    }




    private static class OSProcess extends Thread{
        private String Name;
        public OSProcess(String Name){
            this.Name=Name;
        }


        @Override
        public void run() {

        }


    }
}