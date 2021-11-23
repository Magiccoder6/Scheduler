package com.sh.sheduler;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @FXML
    private Label AddingResult;

    @FXML
    private Label CopingResult;

    @FXML
    private Label DisplayingResult;

    @FXML
    private Circle AddingNotify;//notify Adding status

    @FXML
    private Circle CopingNotify;//notify Copying status

    @FXML
    private Circle DisplayingNotify;//notify Displaying status


    private Map<String,Integer> elems;//list of integer elements
    private int NumOfProcess;//Number of processes
    private int StartPosition=1;//starting position to access elements
    private ConcurrentLinkedQueue<OSProcess> processes;//stores all processes in a queue
    private  ObservableList<OSProcess> ActiveProcesses = FXCollections.observableArrayList();
    private LinkedList<OSProcess> Terminated =new LinkedList<OSProcess>();
    private LinkedList<OSProcess> TempActiveProcs =new LinkedList<>();

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

    //function to start simulation
    @FXML
    void startSimulation(ActionEvent event) {
        new ProcessScheduler().startScheduler();
    }


    private void saveProcesses(){
        processes= new ConcurrentLinkedQueue<>();
        int  num = (int) ProcessSelector.getValue();//get the number of processes

       for(int i=0;i<num;i++){
            processes.add(new OSProcess("P"+(i+1)));//add all processes to a queue

        }

        for(int i=0;i<5;i++){
            OSProcess temp = processes.poll();
           ActiveProcesses.add(temp);//add 5 processes to active process list
            TempActiveProcs.add(temp);//add the same 5 processes to temporary proc holder
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



//OS model
    private  class OSProcess {

        private String Name;
        private int PID;
        private int TASK;
        private int StartTime;
        private int EndTime;
        private int Attempts;
        private String Before;
        private String After;
        private int index1;
        private int index2;


        public OSProcess(String Name){
            this.Name=Name;
            PID=(int) ((Math.random() * (300000 - 1)) + 1);
            Attempts=1;
            Before=Elements.getText();
        }

        private void runTask(){
           int tasknum = getTaskNumber();
           TASK=tasknum;

            Platform.runLater(() -> {
                switch (tasknum){
                    case 1:
                        AddingNotify.setFill(Color.GREEN);
                        addingFunction(StartPosition);
                        break;
                    case 2:
                        CopingNotify.setFill(Color.GREEN);
                        break;
                    case 3:
                        DisplayingNotify.setFill(Color.GREEN);
                        break;
                }

            });


        }

        private void addingFunction(int position){
            int num1=(int) ((Math.random() * (10 - 1)) + 1);
            int num2=(int) ((Math.random() * (10 - 1)) + 1);

            int element1 = elems.get("EL"+num1);
            int element2 = elems.get("EL"+num2);
            index1=element1;
            index2=element2;

            int result = element1+element2;
            System.out.println(element1);
            System.out.println(element2);
            System.out.println(result);

        }

        //generate a random task number
        private int getTaskNumber(){
            AddingNotify.setFill(Color.RED);
            CopingNotify.setFill(Color.RED);
            DisplayingNotify.setFill(Color.RED);

            return (int) ((Math.random() * (3 - 1)) + 1);
        }
    }

    //class to schedule all processes
    private class ProcessScheduler {

        public ProcessScheduler(){

        }

        public void startScheduler(){
            Task<Void> threadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    OSProcess temp = TempActiveProcs.poll();
                    if(temp==null || processes==null){
                        ActiveProcesses.clear();
                        return null;
                    }


                    temp.runTask();//run the process task
                    OSProcess newProc = processes.poll();//get the next process

                    Platform.runLater(() -> {
                        Terminated.add(temp);
                        ActiveProcesses.remove(temp);//remove from active processes
                        ActiveProcesses.add(newProc);//add new process to active processes
                        TempActiveProcs.add(newProc);//add new process to temporary active process list

                    });

                    Thread.sleep(3000);
                    return null;
                }
            };

            threadTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {

                    if(!ActiveProcesses.isEmpty()){
                        startScheduler();

                    }

                }
            });
            new Thread(threadTask).start();
        }

        /*@Override
        public void run() {
            try {

                while(true){
                    OSProcess temp = TempActiveProcs.poll();
                    if(temp==null || processes==null)
                        break;

                        temp.runTask();
                        OSProcess newProc = processes.poll();

                        Platform.runLater(() -> {

                            ActiveProcesses.remove(temp);
                            ActiveProcesses.add(newProc);
                            TempActiveProcs.add(newProc);

                        });

                     Thread.sleep(2000);


                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }*/
    }
}