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
import java.time.LocalDate;
import java.time.LocalTime;
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
    private Label AddingNum1;

    @FXML
    private Label AddingNum2;

    @FXML
    private Circle CopingNotify;//notify Copying status
    @FXML
    private Label CPrevious;
    @FXML
    private Label CUpdated;
    @FXML
    private Label UIndex;
    @FXML
    private Label PIndex;

    @FXML
    private Circle DisplayingNotify;//notify Displaying status


    private Map<String,Integer> elems;//list of integer elements
    private int NumOfProcess;//Number of processes
    private int StartPosition=0;//starting position to access elements
    private ConcurrentLinkedQueue<OSProcess> processes;//stores all processes in a queue
    private  ObservableList<OSProcess> ActiveProcesses = FXCollections.observableArrayList();
    private LinkedList<OSProcess> Terminated =new LinkedList<OSProcess>();
    private LinkedList<OSProcess> TempActiveProcs =new LinkedList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //add 10 numbers to combo box
        for(int i=0;i<10;i++){
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
            processes.add(new OSProcess("P"+i));//add all processes to a queue

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

        for(int i=0;i<10;i++){
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

    private  void resetColors(){
        AddingNotify.setFill(Color.RED);
        CopingNotify.setFill(Color.RED);
        DisplayingNotify.setFill(Color.RED);
    }



//OSProcess model
    private  class OSProcess {

        private String Name;
        private int PID;
        private int TASK;
        private String StartTime;
        private String EndTime;
        private int Attempts;
        private String Before;
        private String After;
        private int element1;
        private int element2;


        public OSProcess(String Name){
            this.Name=Name;
            PID=(int) ((Math.random() * (300000 - 1)) + 1);
            Attempts=1;
            Before=elems.toString();
        }

        private void runTask(){
           StartTime= LocalTime.now().toString();
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
                        copyingFunction(StartPosition);
                        break;
                    case 3:

                        DisplayingNotify.setFill(Color.GREEN);
                        break;
                }

            });

            EndTime= LocalTime.now().toString();
            After=elems.toString();
        }

        private void addingFunction(int position){
            int num1=(int) ((Math.random() * 9));
            int num2=(int) ((Math.random() * 9));

            int element1 = elems.get("EL"+num1);
            int element2 = elems.get("EL"+num2);
            this.element1=element1;
            this.element2=element2;
            AddingNum1.setText("EL"+num1);
            AddingNum2.setText("EL"+num2);

            int result = element1+element2;

            if(position==9) {//if position equal 10 add the result to the first position
                int el1=elems.get("EL1");
                elems.replace("EL1",(el1+result));
            }else{
                int el=elems.get("EL"+(position+1));
                elems.replace("EL"+(position+1),(result+el));
            }

            AddingResult.setText(elems.values().toString());

        }

        private void copyingFunction(int position){
            int prevLocationIndex=(int) ((Math.random() * 9));
            PIndex.setText(Integer.toString(prevLocationIndex));
            int prevContent=elems.get("EL"+prevLocationIndex);
            CPrevious.setText(Integer.toString(prevContent));


            int updLocationIndex=(int) ((Math.random() * 9));
            UIndex.setText(Integer.toString(updLocationIndex));
            int updContent = elems.get("EL"+updLocationIndex);
            CUpdated.setText(Integer.toString(updContent));
            elems.replace("EL"+updLocationIndex,prevContent);
            CopingResult.setText(elems.values().toString());

            element1=elems.get("EL"+prevLocationIndex);
            element2=elems.get("EL"+updLocationIndex);

            //System.out.println(prevLocationIndex);
            //System.out.println(prevContent);
            //System.out.println(updLocationIndex);
            //System.out.println(updContent);
            //System.out.println(elems.toString());
        }

        //generate a random task number
        private int getTaskNumber(){
            resetColors();

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