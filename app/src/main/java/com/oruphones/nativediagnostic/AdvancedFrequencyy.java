package com.oruphones.nativediagnostic;


import android.content.Context;
import android.hardware.SensorManager;

import com.oruphones.nativediagnostic.util.Callback;
import com.oruphones.nativediagnostic.util.DataPassListener;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.VibrationUtilities.Recorder;

import org.pervacio.onediaglib.advancedtest.common.AudioCalculator;
import org.pervacio.onediaglib.diagtests.TestListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;


public class AdvancedFrequencyy {

    private SensorManager sensorManager;


    private static String TAG = AdvancedFrequencyy.class.getSimpleName();
    private Recorder recorder;
    private AudioCalculator audioCalculator;
    int sampleCount = 0;
    int actualSampleCount = 0;

    int passCount=0;


    private  static final int MAX_SAMPLES_COUNT =400;  // increased as this Sony	SO-01H is giving more samples in recording


    int []freqBucket = new int[MAX_SAMPLES_COUNT];
    int []freqBucket2 = new int[MAX_SAMPLES_COUNT];



    String status;
    public AdvancedFrequencyy(Context context, String status) {
        this.status = status;
        DLog.d(TAG, "AdvancedFrequencyy: "+status);

        DLog.d(TAG, "AdvancedFrequencyy ");

        recorder = new Recorder(status,callback);
        audioCalculator = new AudioCalculator();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);



    }
    private TestListener mTestFinishListener;

    public void setTestFinishListener(TestListener mTestFinishListener){
        this.mTestFinishListener= mTestFinishListener;
    }

    public void startTest(){

        DLog.d(TAG,"startTest ");

        passCount=0;
        sampleCount=0;
        actualSampleCount =0;
        Arrays.fill(freqBucket,0);
        Arrays.fill(freqBucket2,0);


        recorder.start();
        // recorder.stop();

        if(Objects.equals(status, "vibration")) {
            DLog.d(TAG, "startTest: " + "After recorder.start");
        }
        DLog.d(TAG,"enter startTest() player and recorder started ");
    }


    TreeSet<Double> duringVib = new TreeSet<>(Collections.reverseOrder());
    TreeSet<Double> beforeVib = new TreeSet<>(Collections.reverseOrder());

    TreeSet<Double> afterVib = new TreeSet<>(Collections.reverseOrder());

    ArrayList<Double> duringVibArray = new ArrayList<>();
    ArrayList<Double> beforeVibArray = new ArrayList<>();

    ArrayList<Double> afterVibArray = new ArrayList<>();

    ArrayList<Double> duringVibArrayoriginal = new ArrayList<>();
    ArrayList<Double> beforeVibArrayoriginal = new ArrayList<>();

    ArrayList<Double> afterVibArrayoriginal = new ArrayList<>();


    static double totalbvibration=0;

    double totalavibration=0;

    static double totalDvibration=0;

    static double totalbvibrationG=0;

    double totalavibrationG=0;

    static double totalDvibrationG=0;

    private static final DecimalFormat DF_round = new DecimalFormat("0.00");

    ArrayList<Double> slicedArraybefore = new ArrayList<>() ;
    ArrayList<Double> slicedArrayduring= new ArrayList<>() ;
    ArrayList<Double> slicedArrayafter= new ArrayList<>() ;

    private double average(List<Double> numbers) {
        return numbers.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    private Callback callback = new Callback() {

        @Override
        public void onBufferAvailable(byte[] buffer,String s) {

            DLog.d(TAG, "\nonBufferAvailable Status "+status+"\nS = "+s);

            DLog.d(TAG, "onBufferAvailable: ");
            audioCalculator.setBytes(buffer);
            int amplitude = audioCalculator.getAmplitude();

            if(Objects.equals(status, "start")) {

                DLog.d(TAG, "onBufferAvailable if start "+status);

                if(Objects.equals(s, "laststart"))
                {

                    DLog.d(TAG, "onBufferAvailable if laststart "+s);

                    for(int i=4;i<beforeVibArrayoriginal.size();i++)
                    {
                        if(beforeVibArrayoriginal.get(i)>20) {

                            slicedArraybefore.add(beforeVibArrayoriginal.get(i));
                            beforeVib.add(beforeVibArrayoriginal.get(i));
                        }

                    }

                    Iterator<Double> iterator = beforeVib.iterator();
                    for (int i = 0; i < 5 && iterator.hasNext(); i++) {
                        double largest = iterator.next();
                        beforeVibArray.add(largest);
                    }
                    totalbvibration=average(slicedArraybefore);
                    totalbvibrationG=average(beforeVibArray);
                    DLog.d(TAG, "laststart ");

                    DLog.d(TAG, "\n04/10/2023 Values Before Vibration"+
                            "\nOriginal Array : "+slicedArraybefore.toString() +
                            "\nSorted Array : "+beforeVibArray.toString()+
                            "\nTotal Average Value of Original Array:- " +DF_round.format(totalbvibration)+
                            "\nAverage of first 5 greatest values : "+DF_round.format(totalbvibrationG));


                }

                else {
                    DLog.d(TAG, " adding into laststart");
                    double deciblebefore= Double.parseDouble(DF_round.format(20 * Math.log10(amplitude)));
                    beforeVibArrayoriginal.add(deciblebefore);
                }

            }



            else if(Objects.equals(status, "vibration")) {
                DLog.d(TAG, "onBufferAvailable status vibration "+s);

                if(Objects.equals(s, "lastvibration"))
                {
                    DLog.d(TAG, "onBufferAvailable if lastvibration "+s);

                    for(int i=4;i<duringVibArrayoriginal.size();i++)
                    {
                        if(duringVibArrayoriginal.get(i)>20) {

                            slicedArrayduring.add(duringVibArrayoriginal.get(i));
                            duringVib.add(duringVibArrayoriginal.get(i));
                        }
                    }

                    Iterator<Double> iterator = duringVib.iterator();
                    for (int i = 0; i < 5 && iterator.hasNext(); i++) {
                        double largest = iterator.next();
                        duringVibArray.add(largest);
                    }
                    totalDvibration=average(slicedArrayduring);
                    totalDvibrationG=average(duringVibArray);
                    DLog.d(TAG, "get(0)==0.0 lastvibration ");

                    DLog.d(TAG, "\n04/10/2023 Values During Vibration"+
                            "\nOriginal Array : "+slicedArrayduring.toString() +
                            "\nSorted Array : "+duringVibArray.toString()+
                            "\nTotal Average Value of Original Array (excluding zero value):- " +DF_round.format(totalDvibration)+
                            "\nAverage of first 5 greatest values : "+DF_round.format(totalDvibrationG));

                }

                else {
                    DLog.d(TAG, " adding into lastvibration ");

                    double decibleduring= Double.parseDouble(DF_round.format(20 * Math.log10(amplitude)));
                    duringVibArrayoriginal.add(decibleduring);

                }

            }


            else if(Objects.equals(status, "end")) {
                DLog.d(TAG, "onBufferAvailable if end "+status);
                if(Objects.equals(s, "lastend"))
                {
                    DLog.d(TAG, "onBufferAvailable lastend "+s);


                    for(int i=4;i<afterVibArrayoriginal.size();i++)
                    {
                        if(afterVibArrayoriginal.get(i)>20) {
                            DLog.d(TAG, "IndexOutOfBoundsException test: i = " + i);
                            slicedArrayafter.add(afterVibArrayoriginal.get(i));
                            afterVib.add(afterVibArrayoriginal.get(i));
                        }
                    }
                    Iterator<Double> iterator = afterVib.iterator();
                    for (int i = 0; i < 5 && iterator.hasNext(); i++) {
                        double largest = iterator.next();
                        afterVibArray.add(largest);

                    }
                    totalavibration=average(slicedArrayafter);
                    totalavibrationG=average(afterVibArray);

                    DLog.d(TAG, "07/test onBufferAvailable: \ntotalbvibration = "+totalbvibration+"\ntotalavibration= "+totalavibration+"\ntotalDvibration= "+totalDvibration);
                    DLog.d(TAG, "07/test onBufferAvailable: \ntotalbvibrationG = "+totalbvibrationG+"\ntotalavibrationG= "+totalavibrationG+"\ntotalDvibrationG= "+totalDvibrationG);

                    double diff = totalDvibration - ((totalbvibration+totalavibration)/2);
                    double diff_G= totalDvibrationG -( (totalbvibrationG+totalavibrationG)/2);

                    processValue(diff,diff_G);


                    DLog.d(TAG, "\n04/10/2023 Values After Vibration"+
                            "\nOriginal Array : "+slicedArrayafter.toString() +
                            "\nSorted Array : "+afterVibArray.toString()+
                            "\nTotal Average Value of Original Array:- " +DF_round.format(totalavibration)+
                            "\nAverage of first 5 greatest values : "+DF_round.format(totalavibrationG));

                    DLog.d(TAG, "04/10/2023\nDifference: "+diff+"\nGreatest values Difference = "+diff_G);
                }


                else {
                    DLog.d(TAG, " adding into lastend ");
                    double decibleafter= Double.parseDouble(DF_round.format(20 * Math.log10(amplitude)));
                    afterVibArrayoriginal.add(decibleafter);

                }
            }

        }


    };


    private DataPassListener valueListener;

    // Setter method to set the value listener
    public void setValueListener(DataPassListener valueListener) {
        this.valueListener = valueListener;
    }
    public void processValue(Double a,Double b) {
        // Perform some logic in classA
        // ...

        // Check if the valueListener is set and pass the value
        if (valueListener != null) {
            valueListener.onDataPass(a,b);
        }
    }






}