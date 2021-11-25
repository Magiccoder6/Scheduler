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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


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

    @FXML
    private Button ResetButton;//reset button

    @FXML
    private Button StartButton;//start button

    //container to hold report
    @FXML
    private Pane ReportContainer;

    //table to show sequence report
    @FXML
    private TableView<OSProcess> SequenceTable;
    @FXML
    private TableColumn<OSProcess, String> Sequence1;
    @FXML
    private TableColumn<OSProcess, String> Sequence2;

    //process table model
    @FXML
    private TableView<OSProcess> ProcessTable;
    @FXML
    private TableColumn<OSProcess, String> EndTime;
    @FXML
    private TableColumn<OSProcess, String> Name;
    @FXML
    private TableColumn<OSProcess, Integer> PID;
    @FXML
    private TableColumn<OSProcess, Integer> Task;
    @FXML
    private TableColumn<OSProcess, String> StartTime;
    @FXML
    private TableColumn<OSProcess, String> After;
    @FXML
    private TableColumn<OSProcess, Integer> Attempts;
    @FXML
    private TableColumn<OSProcess, String> Before;
    @FXML
    private TableColumn<OSProcess, String> El1;
    @FXML
    private TableColumn<OSProcess, String> El2;

    private Map<String,Integer> elems;//list of integer elements
    private int NumOfProcess;//Number of processes
    private int StartPosition=0;//starting position to access elements
    private ConcurrentLinkedQueue<OSProcess> processes;//stores all processes in a queue
    private  ObservableList<OSProcess> ActiveProcesses = FXCollections.observableArrayList();
    private  ObservableList<OSProcess> Report = FXCollections.observableArrayList();
    private LinkedList<OSProcess> TempActiveProcs;
    private Random rn;

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

        //initialize report table
        initTable();

    }

    //function to start simulation
    @FXML
    void startSimulation(ActionEvent event) {
        disableSelectors(true);
        new ProcessScheduler().startScheduler();
    }

    //function to reset simulation
    @FXML
    void resetSimulation(ActionEvent event) {
        resetColors();
        elems=generateNumbers();//save elements
        Numbers.setText(elems.values().toString());//set elements to label
        Elements.setText(elems.toString());//set integers to label


        saveProcesses();//save processes to data structure

        AddingNum1.setText("");
        AddingNum2.setText("");
        AddingResult.setText("");
        CopingResult.setText("");
        CPrevious.setText("");
        CUpdated.setText("");
        Report.clear();
    }

    //function to close the report
    @FXML
    void CloseReport(ActionEvent event) {
        disableSelectors(false);
        ReportContainer.setVisible(false);
    }

    private void initTable(){
        //Sequence table init
        Sequence1.setCellValueFactory(new PropertyValueFactory<>("ElementSeq1"));
        Sequence2.setCellValueFactory(new PropertyValueFactory<>("ElementSeq2"));

        //Sequence table init
        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        PID.setCellValueFactory(new PropertyValueFactory<>("PID"));
        Task.setCellValueFactory(new PropertyValueFactory<>("TASK"));
        StartTime.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        EndTime.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        Attempts.setCellValueFactory(new PropertyValueFactory<>("Attempts"));
        Before.setCellValueFactory(new PropertyValueFactory<>("Before"));
        After.setCellValueFactory(new PropertyValueFactory<>("After"));
        El1.setCellValueFactory(new PropertyValueFactory<>("ElementSeq1"));
        El2.setCellValueFactory(new PropertyValueFactory<>("ElementSeq2"));

    }

    private void saveTableData(){
        SequenceTable.setItems(Report);
        ProcessTable.setItems(Report);
    }

    private void saveProcesses(){
        processes= new ConcurrentLinkedQueue<>();
        TempActiveProcs=new LinkedList<>();

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
            int nums = new Random().nextInt(99);
            temp.put("EL"+ i,nums);

        }

        return temp;
    }

    //called when the user select number of processes
    @FXML
    void updateProcess(MouseEvent event) {

        NumOfProcess = (int) ProcessSelector.getValue();
        ProcessNum.setText(Integer.toString(NumOfProcess));
        ActiveProcesses.clear();
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

    private void disableSelectors(boolean disable){
        ProcessSelector.setDisable(disable);
       NumberSelector.setDisable(disable);
       StartButton.setDisable(disable);
       ResetButton.setDisable(disable);
    }




//OSProcess model
    public  class OSProcess {

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
        private String ElementSeq1;
        private String ElementSeq2;


        public OSProcess(String Name){
            this.Name=Name;
            PID=new Random().nextInt(857777668);
            Attempts=1;
            Before=elems.toString();
            this.ElementSeq1="";
        }

        private void runTask(){
           StartTime= LocalTime.now().toString();
           int tasknum = getTaskNumber();
           TASK=tasknum;

            Platform.runLater(() -> {
                switch (tasknum) {
                    case 1 -> {
                        AddingNotify.setFill(Color.GREEN);
                        addingFunction(StartPosition);
                    }
                    case 2 -> {
                        CopingNotify.setFill(Color.GREEN);
                        copyingFunction(StartPosition);
                    }
                    case 3 ->{
                        DisplayingNotify.setFill(Color.GREEN);
                        displayFunction();
                    }
                }

            });

            EndTime= LocalTime.now().toString();
            After=elems.toString();
        }

        private void addingFunction(int position){
            int num1=new Random().nextInt(9);
            int num2=new Random().nextInt(9);

            ElementSeq1="EL"+num1;
            ElementSeq2="EL"+num2;

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
            int prevLocationIndex=position;
            PIndex.setText(Integer.toString(prevLocationIndex));
            int prevContent=elems.get("EL"+prevLocationIndex);
            CPrevious.setText(Integer.toString(prevContent));


            int updLocationIndex=new Random().nextInt(9);
            UIndex.setText(Integer.toString(updLocationIndex));
            int updContent = elems.get("EL"+updLocationIndex);
            CUpdated.setText(Integer.toString(updContent));
            elems.replace("EL"+updLocationIndex,prevContent);
            CopingResult.setText(elems.values().toString());

            element1=elems.get("EL"+prevLocationIndex);
            element2=elems.get("EL"+updLocationIndex);

            ElementSeq1="EL"+prevLocationIndex;
            ElementSeq2="EL"+updLocationIndex;
        }

        private void displayFunction(){
            rn = new Random();
            int num = rn.nextInt(9);
            DisplayingResult.setText("Element:"+num +":"+elems.get("EL"+num));
            ElementSeq1="EL"+num;
            ElementSeq2="EL"+num;
        }

        //generate a random task number
        private int getTaskNumber(){
            resetColors();
            rn = new Random();
            int num = rn.nextInt(3 - 1 + 1)+1;
            if(num==0)
                num=3;
            return num;
        }

    public String getName() {
        return Name;
    }

    public int getPID() {
        return PID;
    }

    public int getTASK() {
        return TASK;
    }

    public String getStartTime() {
        return StartTime;
    }


    public String getEndTime() {
        return EndTime;
    }


    public int getAttempts() {
        return Attempts;
    }


    public String getBefore() {
        return Before;
    }


    public String getAfter() {
        return After;
    }


    public int getElement1() {
        return element1;
    }


    public int getElement2() {
        return element2;
    }


    public String getElementSeq1() {
        return ElementSeq1;
    }


    public String getElementSeq2() {
        return ElementSeq2;
    }

}

    //class to schedule all processes
    private class ProcessScheduler {

        public ProcessScheduler(){

        }

        public void startScheduler(){
            Task<Void> threadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    OSProcess temp = TempActiveProcs.poll();
                    if (temp == null || processes == null) {
                        ActiveProcesses.clear();
                        return null;
                    }


                    temp.runTask();//run the process task
                    OSProcess newProc = processes.poll();//get the next process

                    Platform.runLater(() -> {
                        Report.add(temp);
                        ActiveProcesses.remove(temp);//remove from active processes
                        ActiveProcesses.add(newProc);//add new process to active processes
                        TempActiveProcs.add(newProc);//add new process to temporary active process list

                    });

                    Thread.sleep(3000);
                    return null;
                }
            };

            threadTask.setOnSucceeded(event -> {

                if(!ActiveProcesses.isEmpty()){
                    startScheduler();

                }else {
                    saveTableData();//add process data to table
                    ReportContainer.setVisible(true);
                }

            });
            new Thread(threadTask).start();
        }

    }
}