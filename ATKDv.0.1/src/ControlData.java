import javafx.scene.control.ComboBox;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import static Pahan.util.MathOperations.*;
import  static Pahan.util.SwingConsole.*;
import static Pahan.util.WorkWithFiles.*;
/**
 * Created by Pahan on 10.10.2016.
 */
public class ControlData extends JFrame {
    private File inputATKD;
    private File inputRawFile;
    private boolean cutChose=false;
    private boolean medfiltrChose=true;
    private static Logger log = Logger.getLogger(ControlData.class.getName());
    //Choices for data type Combo Box
    private String [] dataDescription={"ATKD Ordinary Data","ATKD with Temperature Control",
            "ATKD Ordinary Data with SRS810","ATKD Temperature Control with SRS810",
            "Raw Data of Temperature"};
    //Choices for Combo Box which responsible for action for comma and points it the input file\files
    private String [] commaPointDescription={"Do nothing","Change comma to point", "Change point to comma"};
    //Buttons
    private JButton Calculate=new JButton("Calculate data");
    private JButton EraseFiles = new JButton("Erase Files");
    private JButton HelpButton=new JButton("Help");
    private JButton AdvancedPAnel=new JButton("Start advanced panel");
    //Check Boxes
    private JCheckBox cutCheckBox=new JCheckBox();
    private JLabel cutBoxLabel=new JLabel();
    private JCheckBox medfiltr=new JCheckBox();
    private JLabel medfiltrLabel=new JLabel();
    //Choosers
    private JFileChooser inputFileChooser= new JFileChooser();
    private JFileChooser rawFileChooser=new JFileChooser();
    //Text fields and labels for them
    private JTextField fileNameInput=new JTextField(10);
    private JLabel inpuLabel=new JLabel();
    private JTextField fileNameRaw=new JTextField(10);
    private JLabel fileRawLabel=new JLabel();
    private JTextField TimeStep=new JTextField(10);
    private JLabel timeStepLabel=new JLabel();
    private JTextField SPS=new JTextField();
    private JLabel SPSLabel=new JLabel();
    private JTextField allowedError=new JTextField();
    private JLabel allowedErrorLabel=new JLabel();
    private JTextField deltaT=new JTextField();
    private JLabel deltaTLabel=new JLabel();
    private JTextField maxTempRange=new JTextField();
    private JLabel maxTempRangeLabel=new JLabel();
    //Combo boxes(and text fields for them)
    private JComboBox DataType=new JComboBox();
    private JTextField ForDataType=new JTextField(15);
    private JComboBox commaAndPoint=new JComboBox();
    private JTextField ForCommaAndPoint=new JTextField(15);
    //Additional window, which appears when the button "Show help" is pressed.
    private HelpMessage help= new HelpMessage();
    /**
     * Auxiliary method for comma to point changing and vice versa.
     * @param symbolChange- index of selected choice in the combo box for comma and points
     */
    private void comma2point(int symbolChange,String fileName){
        try{
            File file=new File(fileName);
            if(file.exists()||file.isFile()) {
                if (1 == symbolChange) {
                    ATKD.comma2point(fileName);
                } else if (2 == symbolChange) {
                    ATKD.point2comma(fileName);
                }
            }
        }catch (IOException e){
            log.log(Level.SEVERE,"IOException: ",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that can hide all text fields and labels for them except those for combo boxes
     */
    public void hideAll(){
        fileNameInput.setVisible(false);
        inpuLabel.setVisible(false);
        TimeStep.setVisible(false);
        timeStepLabel.setVisible(false);
        fileNameRaw.setVisible(false);
        fileRawLabel.setVisible(false);
        fileNameInput.setVisible(false);
        SPS.setVisible(false);
        SPSLabel.setVisible(false);
        allowedError.setVisible(false);
        allowedErrorLabel.setVisible(false);
        deltaT.setVisible(false);
        deltaTLabel.setVisible(false);
        maxTempRange.setVisible(false);
        maxTempRangeLabel.setVisible(false);
    }

    /**
     * Method that invoke window. It is similar for method from package Pahan.util.SwingConsole with the difference
     * that in this class DefaultCloseOperation was set to HIDE_ON_CLOSE
     * @param f- frame (usually class that extends JFrame) to invoke
     * @param width- width of appearing window
     * @param height-height of appearing window
     */
    public static void runHelp(final JFrame f, final  int width,final int height){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                f.setTitle(f.getClass().getSimpleName());
                f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                f.setSize(width,height);
                f.setVisible(true);
            }
        });
    }
    class startListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            runHelp(new AdvancedControlData(),750,400);
        }
    }
    /**
     * Action Listener for check box for medfiltr
     */
    class medfiltrCheckListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(medfiltr.isSelected()){
                medfiltrChose=true;
            }else {
                medfiltrChose=false;
            }
        }
    }
    /**
     * Action Listener for check box for data cut
     */
    class checkBoxListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(cutCheckBox.isSelected()){
                cutChose=true;
            }else{
                cutChose=false;
            }
        }
    }
    /**
     *Action Listener for help window
     */
    class helpListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            runHelp(help,1200,700);
        }
    }

    /**
     * Action Listener for Combo Box which is responsible for action with comma and points
     */
    class commaAndPointListener implements ActionListener{
        String fileNameInitial="ATKD.txt";
        String fileRaw="Raw_data_temperature.txt";
        @Override
        public void actionPerformed(ActionEvent e) {
            ForCommaAndPoint.setText("index:"+commaAndPoint.getSelectedIndex()+" "
                    +((JComboBox)e.getSource()).getSelectedItem());
            if(!fileNameInput.getText().isEmpty()) fileNameInitial=fileNameInput.getText();
            comma2point(commaAndPoint.getSelectedIndex(),fileNameInitial);
            JOptionPane.showMessageDialog( new ControlData(),"Operation for comma and point was performed");
            if(!fileNameRaw.getText().isEmpty()) fileRaw=fileNameRaw.getText();
            comma2point(commaAndPoint.getSelectedIndex(),fileRaw);
        }
    }
    /**
     * Action Listener for Combo Box which is responsible for data type selection
     */
    class dataTypeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            ForDataType.setText("index:"+DataType.getSelectedIndex()+" "
                    +((JComboBox)e.getSource()).getSelectedItem());
            int dataType=DataType.getSelectedIndex();
            switch (dataType){
                /**For ATKD ordinary data
                 * We need: input file (1 parameter)
                 * Useless for this data type: time step, raw data file, delta T,Maximum temperature range,
                 * Samples per period, allowed value of error (6 parameters)
                 */
                case 0:
                    fileNameInput.setVisible(true);
                    inpuLabel.setVisible(true);
                    medfiltr.setVisible(true);
                    medfiltrLabel.setVisible(true);
                    timeStepLabel.setVisible(false);
                    TimeStep.setVisible(false);
                    fileRawLabel.setVisible(false);
                    fileNameRaw.setVisible(false);
                    deltaT.setVisible(false);
                    deltaTLabel.setVisible(false);
                    maxTempRange.setVisible(false);
                    maxTempRangeLabel.setVisible(false);
                    SPS.setVisible(false);
                    SPSLabel.setVisible(false);
                    allowedError.setVisible(false);
                    allowedErrorLabel.setVisible(false);
                    cutBoxLabel.setVisible(false);
                    cutCheckBox.setVisible(false);
                    break;
                /**For ATKD Temperature control
                 * We need: input file,deltaT, Maximum temperature range (3 parameters)
                 * Useless for this data type: time step, raw data file, samples per period,
                 * allowed value of error(4 parameters)
                 */
                case 1:
                    fileNameInput.setVisible(true);
                    inpuLabel.setVisible(true);
                    medfiltr.setVisible(true);
                    medfiltrLabel.setVisible(true);
                    deltaTLabel.setBounds(300,115,125,25);
                    deltaTLabel.setVisible(true);
                    deltaT.setBounds(300,155,125,25);
                    deltaT.setVisible(true);
                    maxTempRangeLabel.setBounds(300,215,200,25);
                    maxTempRangeLabel.setVisible(true);
                    maxTempRange.setBounds(300,255,125,25);
                    maxTempRange.setVisible(true);
                    timeStepLabel.setVisible(false);
                    TimeStep.setVisible(false);
                    fileRawLabel.setVisible(false);
                    fileNameRaw.setVisible(false);
                    SPS.setVisible(false);
                    SPSLabel.setVisible(false);
                    allowedError.setVisible(false);
                    allowedErrorLabel.setVisible(false);
                    cutBoxLabel.setVisible(false);
                    cutCheckBox.setVisible(false);
                    break;
                /**For ATKD ordinary data with dT obtained from SRS810:
                 * We need: input file, samples per period (2 parameters)
                 * Useless for this data type: time step, raw data file, delta T,Maximum temperature range,
                 * allowed value of error (5 parameters)
                 */
                case 2:
                    fileNameInput.setVisible(true);
                    inpuLabel.setVisible(true);
                    medfiltr.setVisible(true);
                    medfiltrLabel.setVisible(true);
                    SPSLabel.setBounds(300,115,200,25);
                    SPSLabel.setVisible(true);
                    SPS.setBounds(300,155,125,25);
                    SPS.setVisible(true);
                    //Check Box
                    cutBoxLabel.setBounds(450,15,125,25);
                    cutBoxLabel.setVisible(true);
                    cutCheckBox.setBounds(500,40,50,50);
                    cutCheckBox.setVisible(true);
                    timeStepLabel.setVisible(false);
                    TimeStep.setVisible(false);
                    fileRawLabel.setVisible(false);
                    fileNameRaw.setVisible(false);
                    deltaT.setVisible(false);
                    deltaTLabel.setVisible(false);
                    maxTempRange.setVisible(false);
                    maxTempRangeLabel.setVisible(false);
                    allowedError.setVisible(false);
                    allowedErrorLabel.setVisible(false);
                    break;
                /**For ATKD Temperature control with dT obtained from SRS810:
                 * We need: input file,delta T, Maximum temperature range, samples per period (4 parameters)
                 * Useless for this data type: time step, raw data file, allowed value of error (3 parameters)
                 */
                case 3:
                    fileNameInput.setVisible(true);
                    inpuLabel.setVisible(true);
                    medfiltr.setVisible(true);
                    medfiltrLabel.setVisible(true);
                    deltaTLabel.setBounds(300,115,125,25);
                    deltaTLabel.setVisible(true);
                    deltaT.setBounds(300,155,125,25);
                    deltaT.setVisible(true);
                    maxTempRangeLabel.setBounds(300,215,200,25);
                    maxTempRangeLabel.setVisible(true);
                    maxTempRange.setBounds(300,255,125,25);
                    maxTempRange.setVisible(true);
                    SPSLabel.setBounds(300,305,200,25);
                    SPSLabel.setVisible(true);
                    SPS.setBounds(300,345,200,25);
                    SPS.setVisible(true);
                    cutBoxLabel.setBounds(450,15,125,25);
                    cutBoxLabel.setVisible(true);
                    cutCheckBox.setBounds(500,40,50,50);
                    cutCheckBox.setVisible(true);
                    timeStepLabel.setVisible(false);
                    TimeStep.setVisible(false);
                    fileRawLabel.setVisible(false);
                    fileNameRaw.setVisible(false);
                    allowedError.setVisible(false);
                    allowedErrorLabel.setVisible(false);
                    break;
                /**For ATKD STD Check
                 * We need: raw data file , allowable value of error, time step,
                 * optionally: input file(with temperature and pressure) (3-4 parameters)
                 * Useless for this data type: delta T , Maximum temperature range,samples per period (3 parameters)
                 */
                case 4:
                    fileNameInput.setVisible(true);
                    inpuLabel.setVisible(true);
                    medfiltr.setVisible(true);
                    medfiltrLabel.setVisible(true);
                    fileRawLabel.setBounds(300,115,125,25);
                    fileRawLabel.setVisible(true);
                    fileNameRaw.setBounds(300,155,125,25);
                    fileNameRaw.setVisible(true);
                    timeStepLabel.setBounds(300,215,125,25);
                    timeStepLabel.setVisible(true);
                    TimeStep.setBounds(300,255,125,25);
                    TimeStep.setVisible(true);
                    allowedErrorLabel.setBounds(300,305,125,25);
                    allowedErrorLabel.setVisible(true);
                    allowedError.setBounds(300,345,125,25);
                    allowedError.setVisible(true);
                    SPSLabel.setVisible(true);
                    SPSLabel.setBounds(300,395,200,25);
                    SPS.setBounds(300,435,125,25);
                    SPS.setVisible(true);
                    deltaT.setVisible(false);
                    deltaTLabel.setVisible(false);
                    maxTempRange.setVisible(false);
                    maxTempRangeLabel.setVisible(false);
                    cutBoxLabel.setVisible(false);
                    cutCheckBox.setVisible(false);
                    break;
            }
        }
    }
    /**
     * Action Listener class for erase button.
     */
    class EraseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int dataType=DataType.getSelectedIndex();
            System.gc();
            switch (dataType) {
                case 0:
                    ATKDOrdinaryData ordinaryData=new ATKDOrdinaryData();
                    ordinaryData.deleteAllFiles();
                    ordinaryData.deleteAllFiles();
                    break;
                case 1:
                    ATKDTemperatureControl temperatureControl=new ATKDTemperatureControl();
                    temperatureControl.deleteAllFiles();
                    temperatureControl.deleteAllFiles();
                    break;
                case 2:
                    ATKDOrdinarySRS810dT ordinarySRS810dT=new ATKDOrdinarySRS810dT();
                    ordinarySRS810dT.deleteAllFiles();
                    ordinarySRS810dT.deleteAllFiles();
                    break;
                case 3:
                    ATKDTemperatureControlSRS810dT temperatureControlSRS810dT=new ATKDTemperatureControlSRS810dT();
                    temperatureControlSRS810dT.deleteAllFiles();
                    temperatureControlSRS810dT.deleteAllFiles();
                    break;
                case 4:
                    StdCheck stdCheck=new StdCheck();
                    stdCheck.deleteAllFiles();
                    stdCheck.deleteAllFiles();
                    break;
            }
        }
    }
    /**
     *Action Listener for Calculate Button
     */
    class  CalculateListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            int dataType=DataType.getSelectedIndex();
            int symbolChange=commaAndPoint.getSelectedIndex();
            double deltaTValue=0.1;
            double maxRangeValue=0.9;
            int samplesPerPeriod=1;
            int timeStep=3;
            String fileNameInitial="ATKD.txt";
            String fileRaw="Raw_data_temperature.txt";
            if(!fileNameInput.getText().isEmpty()) fileNameInitial=fileNameInput.getText();
            File fileIn=new File(fileNameInitial);
            try {
                if(fileIn.exists()&&fileIn.isFile()) {
                    ATKD.comma2point(fileNameInitial);
                }
            }catch (IOException exc){
                throw new RuntimeException(exc);
            }
            switch (dataType) {
                //For ATKD ordinary data
                case 0:
                    ATKDOrdinaryData ATKD1 = new ATKDOrdinaryData();
                    log.info("Used initial parameters: \r\n "+"Selected data type: "+DataType.getSelectedItem()
                            +"Name of input file: "+fileNameInitial+"selected action for comma and point: "+
                            commaAndPoint.getSelectedItem());
                    log.info("Used:"+ATKD1.toString());
                    if(fileIn.exists()&&fileIn.isFile()&&ATKD.getData(fileNameInitial).size()%8==0) {
                        ATKD1.calculateAmplitudeParameters(fileNameInitial,medfiltrChose);
                        ATKD1.calculateHarmonics(fileNameInitial,medfiltrChose);
                        JOptionPane.showMessageDialog( new ControlData(),"Data calculated");
                    }else {
                        JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                "Check the presence of the initial file/files and their compliance with the specified data type5");
                    }
                    break;
                //For ATKD temperature control
                case 1:
                    if(!maxTempRange.getText().isEmpty())maxRangeValue= Double.parseDouble(maxTempRange.getText());
                    if(!deltaT.getText().isEmpty()) deltaTValue=Double.parseDouble(deltaT.getText());
                    ATKDTemperatureControl ATKD2=new ATKDTemperatureControl(maxRangeValue,deltaTValue);
                    log.info("Used initial parameters: \r\n "+"Selected data type: "+DataType.getSelectedItem()
                            +"Name of input file: "+fileNameInitial+"selected action for comma and point: "+
                            commaAndPoint.getSelectedItem());
                    log.info("Used:"+ATKD2.toString());
                    if(fileIn.exists()&&fileIn.isFile()) {
                        ATKD2.calculateAmplitudeParameters(fileNameInitial,medfiltrChose);
                        ATKD2.calculateHarmonics(fileNameInitial,medfiltrChose);
                        JOptionPane.showMessageDialog( new ControlData(),"Data calculated");
                    }else {
                        JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                "Check the presence of the initial file/files");
                    }
                    break;
                //For ATKD ordinary data with dT obtained from SRS810
                case 2:
                    if(!SPS.getText().isEmpty()) samplesPerPeriod=Integer.parseInt(SPS.getText());
                    if (samplesPerPeriod<=500) {
                        ATKDOrdinarySRS810dT ATKD3 = new ATKDOrdinarySRS810dT(samplesPerPeriod);
                        log.info("Used initial parameters: \r\n " + "Selected data type: " + DataType.getSelectedItem()
                                + "Name of input file: " + fileNameInitial + "selected action for comma and point: " +
                                commaAndPoint.getSelectedItem());
                        log.info("Used:" + ATKD3.toString());
                        if (fileIn.exists() && fileIn.isFile()) {
                            try {
                                ATKD3.calculateHarmonics(fileNameInitial,medfiltrChose);
                                ATKD3.calculateAmplitudeParameters(fileNameInitial, cutChose,medfiltrChose);
                            }catch (IndexOutOfBoundsException exc){
                                JOptionPane.showMessageDialog(new ControlData(), "Unable to calculate data: incorrect " +
                                        "value of samples per period was entered");
                                log.warning("Unable to start calculation with such parameters:\r\nSPP: "+samplesPerPeriod);
                                ATKD3.deleteAllFiles();
                                throw new RuntimeException(exc);
                            }
                                JOptionPane.showMessageDialog(new ControlData(), "Data calculated");
                        } else {
                            JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                    "Check the presence of the initial file/files");
                        }
                    }else {
                        JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                "The value of SPP must be less or equal to 500");
                        log.warning("Unable to start calculation with such parameters:\r\nSPP: "+samplesPerPeriod);
                    }
                    break;
                //For ATKD Temperature control with dT obtained from SRS810
                case 3:
                    if(!maxTempRange.getText().isEmpty())maxRangeValue= Double.parseDouble(maxTempRange.getText());
                    if(!deltaT.getText().isEmpty()) deltaTValue=Double.parseDouble(deltaT.getText());
                    if(!SPS.getText().isEmpty()) samplesPerPeriod=Integer.parseInt(SPS.getText());
                    if(samplesPerPeriod<=500) {
                        ATKDTemperatureControlSRS810dT ATKD4 = new ATKDTemperatureControlSRS810dT(maxRangeValue, deltaTValue, samplesPerPeriod);
                        log.info("Used initial parameters: \r\n " + "Selected data type: " + DataType.getSelectedItem()
                                + "Name of input file: " + fileNameInitial + "selected action for comma and point: " +
                                commaAndPoint.getSelectedItem());
                        log.info("Used:" + ATKD4.toString());
                        if (fileIn.exists() && fileIn.isFile()) {
                            try {
                                ATKD4.calculateAmplitudeParameters(fileNameInitial, cutChose,medfiltrChose);
                                ATKD4.calculateHarmonics(fileNameInitial,medfiltrChose);
                            } catch (IndexOutOfBoundsException exc) {
                                JOptionPane.showMessageDialog(new ControlData(), "Unable to calculate data: incorrect " +
                                        "value of samples per period was entered");
                                ATKD4.deleteAllFiles();
                                log.warning("Unable to start calculation with such parameters:\r\nSPP: "+samplesPerPeriod);
                                throw new RuntimeException(exc);
                            }
                            JOptionPane.showMessageDialog(new ControlData(), "Data calculated");
                        } else {
                            JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                    "Check the presence of the initial file/files");
                        }
                    }else {
                        JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                "The value of SPP must be less or equal to 500");
                        log.warning("Unable to start calculation with such parameters:\r\nSPP: "+samplesPerPeriod);
                    }
                    break;
                //For ATKD STD Check
                case 4:
                    double allowedErrorValue=7.75;
                    if(!fileNameRaw.getText().isEmpty())fileRaw=fileNameRaw.getText();
                    if (!allowedError.getText().isEmpty()) {
                        allowedErrorValue = Double.parseDouble(allowedError.getText());
                    }
                    if(!SPS.getText().isEmpty()) samplesPerPeriod=Integer.parseInt(SPS.getText());
                    if (!TimeStep.getText().isEmpty()) timeStep = Integer.parseInt(TimeStep.getText());
                    StdCheck ATKD5 = new StdCheck(allowedErrorValue,samplesPerPeriod);
                    log.info("Used initial parameters: \r\n "+"Selected data type : "+DataType.getSelectedItem()
                            +" Name of input file: "+fileNameInitial+" selected action for comma and point: "+
                            commaAndPoint.getSelectedItem()+" Name of raw file: "+fileRaw+" Chosen time step for handling:"+
                            TimeStep.getText());
                    log.info("Used:"+ATKD5.toString());
                    File fileRawData=new File(fileRaw);
                    if(fileRawData.exists()&&fileRawData.isFile()&&0==ATKD.getData(fileNameInitial).size()%3) {
                        try {
                            ATKD.comma2point(fileRaw);
                        } catch (IOException ex) {
                            log.log(Level.SEVERE, "IOException: ", ex);
                            throw new RuntimeException(ex);
                        }
                        if(fileIn.isFile()&&fileIn.exists()){
                            ATKD5.completeAnalysis(fileNameInitial,fileRaw,timeStep,medfiltrChose);
                        }else {
                            ATKD5.handleRawData(fileRaw,timeStep,medfiltrChose);
                        }
                        JOptionPane.showMessageDialog( new ControlData(),"Data calculated");
                    }else{
                        JOptionPane.showMessageDialog(new ControlData(), "Unable to start calculation. " +
                                "Check the presence of the initial file/files and their compliance with the specified data type");
                    }
                    break;
                default:
                    break;
            }

        }
    }
    private int count=0;
    ControlData() {
        inputFileChooser.setFileFilter(new TxtFileFilter());
        rawFileChooser.setFileFilter(new TxtFileFilter());
        for(int i=0;i<dataDescription.length;i++){
            DataType.addItem(dataDescription[count++]);
        }
        count=0;
        for(int j=0;j<commaPointDescription.length;j++){
            commaAndPoint.addItem(commaPointDescription[count++]);
        }
        EraseFiles.addActionListener(new EraseListener());
        Calculate.addActionListener(new CalculateListener());
        DataType.addActionListener(new dataTypeListener());
        HelpButton.addActionListener(new helpListener());
        commaAndPoint.addActionListener(new commaAndPointListener());
        cutCheckBox.addActionListener(new checkBoxListener());
        AdvancedPAnel.addActionListener(new startListener());
        setLayout(null);
        //Left column on panel
        //Buttons
        Calculate.setBounds(5,15,125,30);
        add(Calculate);
        EraseFiles.setBounds(5,105,125,30);
        add(EraseFiles);
        HelpButton.setBounds(5,205,125,30);
        add(HelpButton);
        AdvancedPAnel.setBounds(5,455,200,30);
        add(AdvancedPAnel);
        //Combo boxes
        DataType.setBounds(5,305,230,30);
        add(DataType);
        ForDataType.setEditable(false);
        commaAndPoint.setBounds(5,405,230,30);
        add(commaAndPoint);
        ForCommaAndPoint.setEditable(false);
        //Right column on panel
        //Text Fields and Labels for them
        fileNameInput.setVisible(true);
        inpuLabel.setVisible(true);
        inpuLabel.setText("Name of input file");
        inpuLabel.setBounds(300,15,125,30);
        add(inpuLabel);
        fileNameInput.setBounds(300,55,125,25);
        fileNameInput.setText("ATKD.txt");
        fileNameInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    inputFileChooser.showOpenDialog(new FileChooseDialog());
                    inputATKD=inputFileChooser.getSelectedFile();
                    fileNameInput.setText(inputATKD.getAbsolutePath());
                }
            }
        });
        add(fileNameInput);
        fileRawLabel.setText("Name of Raw File");
        add(fileRawLabel);
        add(fileNameRaw);
        fileNameRaw.setText("Raw_data_temperature.txt");
        fileNameRaw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                if(ev.getButton()==MouseEvent.BUTTON1){
                    rawFileChooser.showOpenDialog(new FileChooseDialog());
                    inputRawFile=rawFileChooser.getSelectedFile();
                    fileNameRaw.setText(inputRawFile.getAbsolutePath());
                }
            }
        });
        deltaTLabel.setText("Delta T");
        add(deltaTLabel);
        add(deltaT);
        deltaT.setText("0.1");
        timeStepLabel.setText("Time Step");
        add(timeStepLabel);
        add(TimeStep);
        TimeStep.setText("3");
        maxTempRangeLabel.setText("Max Temperature Range");
        add(maxTempRangeLabel);
        add(maxTempRange);
        maxTempRange.setText("0.9");
        allowedErrorLabel.setText("Allowable error");
        add(allowedErrorLabel);
        add(allowedError);
        allowedError.setText("7.75");
        SPSLabel.setText("Samples per second or per period");
        add(SPSLabel);
        add(SPS);
        SPS.setText("1");
        //Check Box
        cutBoxLabel.setText("Cut data for SRS810?");
        add(cutBoxLabel);
        add(cutCheckBox);
        medfiltrLabel.setText("Median filtration?");
        medfiltrLabel.setBounds(450,160,125,25);
        medfiltrLabel.setVisible(false);
        medfiltr.setBounds(500,185,50,50);
        medfiltr.setSelected(true);
        medfiltr.setVisible(false);
        medfiltr.addActionListener(new medfiltrCheckListener());
        add(medfiltrLabel);
        add(medfiltr);
    }
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(ControlData.class.getResourceAsStream("logging.properties"));
        }catch (IOException e){
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        ControlData controlData=new ControlData();
        controlData.hideAll();
        run(controlData,600,600);
    }
}
class FileChooseDialog extends JFrame{
    //Choosers
    private JFileChooser inputFileChooser= new JFileChooser();
    private JFileChooser rawFileChooser=new JFileChooser();
    FileChooseDialog(){
        //File Choosers
        //Input ATKD File Chooser
        inputFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        inputFileChooser.setFileFilter(new TxtFileFilter());
        inputFileChooser.setDialogTitle("Choose input file for load");
        inputFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        inputFileChooser.setMultiSelectionEnabled(false);
        inputFileChooser.setVisible(true);
        //Input Raw File Chooser
        rawFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        inputFileChooser.setFileFilter(new TxtFileFilter());
        rawFileChooser.setDialogTitle("Choose input file for load");
        rawFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        rawFileChooser.setMultiSelectionEnabled(false);
        rawFileChooser.setVisible(true);
    }
}
//class for Help Message which appears when button Show Help is pressed
class HelpMessage extends JFrame{
    JTextArea helpTextArea=new JTextArea();
    HelpMessage(){
        helpTextArea.setVisible(true);
        helpTextArea.setText("This program can help you to calculate different parameters such as:\r\nTemperature Response (or dT)," +
                " Pressure(dP), ATKD(Adiabatic Thermal Pressure Coefficient), dT/dP ratio\r\nand harmonics of " +
                " Temperature or\\and Pressure signal for different data types.\r\n\r\n"+"Firstly, please choose the data type" +
                " of your input file\\files. This program can handle 5 types of data:\r\n\r\n" +
                "1.ATKD Ordinary Data. You should chose this variant if you measured only dT and dP simultaneously" +
                "(without T).\r\nThe data can represent constant signal or alternating signal " +
                "( e.g.,measured by Tone Measurement- Labview subprogram), it doesn't matter. " +
                "\r\nThe main point that you must have a file with data contains T,3 harmonics of dP,dP,3 harmonics of dT,dT" +
                "strictly in this order.\r\nAfter calculation you'll obtain files with averaged T,dT,dP,ATKD,dT/dP " +
                "(Amplitudes.txt),\r\naveraged harmonics of pressure (HarmonicsOfPressure.txt),averaged harmonics of temperature" +
                "(HarmonicsOfPressure.txt) and auxiliary files,\r\neach with only one parameter (which was mentioned above)(0-8.txt).\r\n\r\n" +
                "2.ATKD Temperature control. The main difference between this data type and ordinary data is that for data with " +
                "temperature control you ought to measure dT,dP and T simultaneously.\r\nTo start calculation you should specify" +
                "not only the name of input file, but also the values of delta T and Maximum temperature range.\r\n" +
                "DeltaT is the important parameter which is responsible for value of step during calculation. Data will be averaged" +
                "inside the temperature step intervals.\r\nThe number of averaged point that you obtain after program work is " +
                "the number of temperature step intervals.\r\nMaximum temperature range is responsible for the interval " +
                "inside of which will be made calculation.\r\nFor example, if during measurement, your temperature shifted from" +
                "temperature T1 to temperature T2 and you defined maximum temperature range as x, so calculation will be made" +
                "inside [T1,T1+x] segment.\r\n\r\n" +
                "3.ATKD Ordinary Data with SRS810. This data type is similar for ATKD Ordinary data with the exception that " +
                "dT was measured with help of the nanovoltmeter Stanford Research System 810.\r\nInput file" +
                "consists of 5 parameters;T, 3 harmonics of dP, dP,dT.\r\nYour should specify such parameters as Samples per" +
                "period which represent the number of temperature response points per one point of pressure," +
                "harmonics of pressure,pressure and temperature.\r\n\r\n" +
                "4.ATKD Temperature Control with SRS810. This data type is similar for ATKD Temperature Control " +
                "with the exception that dT was measured with help of the nanovoltmeter Stanford Research System 810.\r\n" +
                "Input file consists of 5 parameters;T, 3 harmonics of dP, dP,dT.\r\nYour should specify not onle such parameter " +
                "as Samples per period (as previous case),\r\nbut also DeltaT and Maximum Temperature Range as for" +
                "ATKD Temperature Control case.\r\n\r\n" +
                "5.Raw Data of Temperature. This data type can help you to analyze your temperature response data.\r\n" +
                "This data consist of one or two files. In first case it is file with signal, measured with help of National Instruments ADC" +
                "with frequency of 100000Hz.\r\nIn second case the initial data consists not only of such raw data file." +
                "Initial data have input file with any of other data type and necessary to extract pressure data from it.\r\n" +
                "It is useful if you want not only analyze temperature response data, but also check pressure, ATKD and other parameters.\r\n" +
                "You should to specify name of raw data file, (or not) name of input file(as was mentioned above)," +
                "time step- parameter which is responsible for value of time step during calculation.\r\n" +
                "For example, if you chose value of timeStep=2, real step during calculation will be equal to 2*100000.\r\n" +
                "You should also specify the allowable error(standard deviation) in %. intervals with std more then allowable will be" +
                "not take into account.\r\n\r\n" +
                "Then, please choose action for comma and points int the initial files. Usually it's necessary to change comma to point" +
                "before starting calculation.  " );
        helpTextArea.setEditable(false);
        add(helpTextArea);
    }
}
/**
 * Class for txt filter in file choosers.
 */
class TxtFileFilter extends FileFilter implements java.io.FileFilter{
    public boolean accept(File pathName){
        String name =pathName.getName();
        return
                pathName.isDirectory()||name.endsWith(".txt");
    }
    public String getDescription(){
        return "Text files(*.txt)";
    }
}

/**
 * Advanced panel for different simple operations. This panel is invoked with button "start advanced panel"
 */
class AdvancedControlData extends JFrame{
    private int medFilterBaseValue=3;
    private File fileInput;
    private File tempFile;
    private String resultFileName="result.txt";
    private String TFileName="T.txt";
    private double seebekCoefficientValue=40;
    private double roomTemperatureValue= 293.15;
    private int decimationValue=10;
    private int numberOfSamples=1;
    private String inputFile;
    private String[]  temperatureDescription={"From V to Kelvin","From Kelvin to V"};
    //Buttons and labels for them
    private JButton analyzePressure= new JButton("Analyze dP");
    private JButton reduceButton=new JButton("Decimate Data");
    private JButton medFilter=new JButton("Median Filter");
    private JButton prepareData=new JButton("Prepare data");
    //Chooser
    private JLabel fileHandlingLabel= new JLabel();
    private JTextField fileForHandling= new JTextField();
    private JFileChooser fileForHandlingChooser=new JFileChooser();
    private JLabel TfileLabel=new JLabel();
    private JTextField Tfile=new JTextField();
    private JFileChooser TfileChooser=new JFileChooser();
    // Text Fields
    private JLabel temperatureLabel=new JLabel();
    private JTextField roomTemperature=new JTextField();
    private JLabel seebekLabel=new JLabel();
    private JTextField seebekCoefficient=new JTextField();
    private JLabel decimationLabel =new JLabel();
    private JTextField valueofDecimation=new JTextField();
    private JLabel resultFileLabel=new JLabel();
    private JTextField resultFile= new JTextField();
    private JLabel medFilterBaseLabel= new JLabel();
    private JTextField medFilterBase= new JTextField();
    private JLabel samplesLabel=new JLabel();
    private JTextField samplesNumber=new JTextField();
    //Combo box
    private JComboBox changeTemperature=new JComboBox();
    private JTextField chooseTemperature=new JTextField();
    /**
     * Action Listener for data preparation
     */
    class prepareListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!fileForHandling.getText().isEmpty()){
                inputFile=fileForHandling.getText();
            }
            if(!resultFile.getText().isEmpty()){
                resultFileName=resultFile.getText();
            }
            if(!samplesNumber.getText().isEmpty()){
                numberOfSamples=Integer.parseInt(samplesNumber.getText());
            }
            if(!Tfile.getText().isEmpty()){
                TFileName=Tfile.getText();
            }
            if(new File(inputFile).isFile()&& new File(inputFile).exists()&&new File(TFileName).exists()&&new File(TFileName).isFile()) {
                try {
                    ATKD.comma2point(inputFile);
                    ATKD.comma2point(TFileName);
                }catch (IOException excep){
                    throw new RuntimeException(excep);
                }
                ATKDOrdinarySRS810dT ordinarySRS810dT = new ATKDOrdinarySRS810dT(numberOfSamples);
                ordinarySRS810dT.prepareDataExternalT(inputFile,TFileName,resultFileName);
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Data was prepared");
            }else {
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Unable to start calculation, check the existence of both initial files");
            }
        }
    }
    /**
     * Action listener for median filter button
     */
    class medFilterListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!fileForHandling.getText().isEmpty()){
                inputFile=fileForHandling.getText();
            }
            if(!resultFile.getText().isEmpty()){
                resultFileName=resultFile.getText();
            }
            if(!Tfile.getText().isEmpty()){
                TFileName=Tfile.getText();
            }
            writeTextToFile(resultFileName,"");
            if(new File(inputFile).exists()&&new File(inputFile).isFile()){
                ArrayList<Double> filteredData=medFilter(ATKD.getData(inputFile),medFilterBaseValue);
                for(int i=0;i<filteredData.size();i++){
                    writeTextToFileWithoutRewrite(resultFileName,Double.toString(filteredData.get(i)));
                }
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Filtration produced");
            }else {
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Unable to start calculation: check the existence of input file");
            }
        }
    }
    /**
     * Action listener for pressure analyze button.
     */
    class pressureListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!fileForHandling.getText().isEmpty()) {
                inputFile = fileForHandling.getText();
            }
            if (!resultFile.getText().isEmpty()) {
                resultFileName = resultFile.getText();
            }
            if (new File(inputFile).exists()&&new File(inputFile).isFile()) {
                ATKD.pressureAnalysis(inputFile, resultFileName,false);
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Pressure analyzed");
            }else {
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Unable to start calculation: check the existence of input file");
            }
        }
    }
    /**
     * Action listener for temperature conversion combo box
     */
    class temperatureListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            chooseTemperature.setText("index:"+changeTemperature.getSelectedIndex()+" "
                    +((JComboBox)e.getSource()).getSelectedItem());
            int actionTemperature=changeTemperature.getSelectedIndex();
            if(!roomTemperature.getText().isEmpty()) {
                roomTemperatureValue = Double.parseDouble(roomTemperature.getText());
            }
            if(!seebekCoefficient.getText().isEmpty()){
                seebekCoefficientValue= Double.parseDouble(seebekCoefficient.getText());
            }
            if(!fileForHandling.getText().isEmpty()){
                inputFile=fileForHandling.getText();
            }
            if(!resultFile.getText().isEmpty()){
                resultFileName=resultFile.getText();
            }
            if(new File(inputFile).exists()&&new File(inputFile).isFile()) {
                if (0 == actionTemperature) {
                    ATKD.convertTemperaturemkVtoK(seebekCoefficientValue, roomTemperatureValue,inputFile,resultFileName);
                } else {
                    ATKD.convertTemperatureKtomV(seebekCoefficientValue, roomTemperatureValue,inputFile,resultFileName);
                }
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Temperature converted");
            }else{
                JOptionPane.showMessageDialog(new AdvancedControlData(),"Unable to start calculation: check the existence of input file");
            }
        }
    }
    /**
     * Action listener for decimate data button.
     */
    class decimateListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!fileForHandling.getText().isEmpty()){
                inputFile=fileForHandling.getText();
            }
            if (!valueofDecimation.getText().isEmpty()){
                decimationValue=Integer.parseInt(valueofDecimation.getText());
            }
            if(!resultFile.getText().isEmpty()){
                resultFileName=resultFile.getText();
            }
            if(new File(inputFile).exists()&&new File(inputFile).isFile()) {
                StdCheck.dataReduction(inputFile,resultFileName, decimationValue);
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Data decimated");
            }else {
                JOptionPane.showMessageDialog(new AdvancedControlData(), "Unable to start calculation: check the existence of input file");
            }
        }
    }
    /**
     * Constructor for advanced panel
     */
    AdvancedControlData(){
        setLayout(null);
        int count=0;
        for(int i=0;i<temperatureDescription.length;i++){
            changeTemperature.addItem(temperatureDescription[count++]);
        }
        //Chooser for handling file
        fileForHandlingChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileForHandlingChooser.setFileFilter(new TxtFileFilter());
        fileForHandlingChooser.setDialogTitle("Choose input file for load");
        fileForHandlingChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileForHandlingChooser.setMultiSelectionEnabled(false);
        fileForHandlingChooser.setVisible(true);
        fileForHandlingChooser.setCurrentDirectory(new File("C:\\Users\\Pahan\\IdeaProjects\\ATKD"));
        fileForHandling.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1) {
                    fileForHandlingChooser.showOpenDialog(new FileChooseDialog());
                    fileInput = fileForHandlingChooser.getSelectedFile();
                    fileForHandling.setText(fileInput.getAbsolutePath());
                }
            }
        });
        //Chooser for file with T
        TfileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        TfileChooser.setFileFilter(new TxtFileFilter());
        TfileChooser.setDialogTitle("Choose file with T");
        TfileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        TfileChooser.setMultiSelectionEnabled(false);
        TfileChooser.setVisible(true);
        TfileChooser.setCurrentDirectory(new File("C:\\Users\\Pahan\\IdeaProjects\\ATKD"));
        Tfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    TfileChooser.showOpenDialog(new FileChooseDialog());
                    tempFile=TfileChooser.getSelectedFile();
                    Tfile.setText(tempFile.getAbsolutePath());
                }
            }
        });
        //Text boxes
        decimationLabel.setBounds(5,190,125,25);
        decimationLabel.setText("Value of decimation");
        valueofDecimation.setBounds(5,230,125,25);
        valueofDecimation.setText("10");
        add(decimationLabel);
        add(valueofDecimation);
        seebekLabel.setBounds(155,100,125,25);
        seebekLabel.setText("Seebeck coefficient");
        seebekCoefficient.setBounds(155,140,125,25);
        seebekCoefficient.setText("40");
        add(seebekLabel);
        add(seebekCoefficient);
        temperatureLabel.setBounds(155,190,125,25);
        temperatureLabel.setText("Room temperature");
        roomTemperature.setBounds(155,230,125,25);
        roomTemperature.setText("293.15");
        add(temperatureLabel);
        add(roomTemperature);
        fileHandlingLabel.setBounds(305,5,125,25);
        fileHandlingLabel.setText("File for handling");
        fileForHandling.setBounds(305,45,125,25);
        add(fileForHandling);
        add(fileHandlingLabel);
        resultFileLabel.setBounds(305,190,125,25);
        resultFileLabel.setText("Name of result file");
        resultFile.setBounds(305,230,125,25);
        resultFile.setText("result.txt");
        add(resultFileLabel);
        add(resultFile);
        medFilterBaseLabel.setBounds(455,190,125,25);
        medFilterBaseLabel.setText("Base for medfilter");
        medFilterBase.setBounds(455,230,125,25);
        medFilterBase.setText("3");
        add(medFilterBaseLabel);
        add(medFilterBase);
        samplesLabel.setBounds(605,190,125,25);
        samplesLabel.setText("Samples per period");
        samplesNumber.setBounds(605,230,125,25);
        samplesNumber.setText("1");
        add(samplesLabel);
        add(samplesNumber);
        TfileLabel.setBounds(605,100,125,25);
        TfileLabel.setText("File with T");
        Tfile.setBounds(605,140,125,25);
        Tfile.setText("T.txt");
        add(TfileLabel);
        add(Tfile);
        //Buttons
        reduceButton.setBounds(5,300,125,25);
        reduceButton.addActionListener(new decimateListener());
        add(reduceButton);
        analyzePressure.addActionListener(new pressureListener());
        analyzePressure.setBounds(305,300,125,25);
        add(analyzePressure);
        medFilter.addActionListener(new medFilterListener());
        medFilter.setBounds(455,300,125,25);
        add(medFilter);
        prepareData.setBounds(605,300,125,25);
        prepareData.addActionListener(new prepareListener());
        add(prepareData);
        //ComboBox
        changeTemperature.setBounds(155,300,125,25);
        chooseTemperature.setEditable(false);
        changeTemperature.addActionListener(new temperatureListener());
        add(changeTemperature);
    }
}