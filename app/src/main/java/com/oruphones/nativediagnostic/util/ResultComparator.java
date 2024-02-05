package com.oruphones.nativediagnostic.util;



import com.oruphones.nativediagnostic.models.tests.TestInfo;

import java.util.Comparator;

/**
 * Created by surya Polasanapalli on 16/11/2017.
 */
public class ResultComparator implements Comparator<TestInfo> {

    @Override
    public int compare(TestInfo o1, TestInfo o2) {
       /* String firstResult = o1.getTestResult().toString();
        String secondResult = o2.getTestResult().toString();
        if(TestResult.OPTIMIZED.equalsIgnoreCase(firstResult))
            firstResult = TestResult.PASS;
        if(TestResult.OPTIMIZED.equalsIgnoreCase(secondResult))
            secondResult = TestResult.PASS;
        int result1 = resultOrder.indexOf(firstResult);
        int result2 = resultOrder.indexOf(secondResult);*/
        /*if (o1.getRespectiveResultSortingIndex() < o2.getRespectiveResultSortingIndex())
            return -1;
        else if(o1.getRespectiveResultSortingIndex()==result2)
           return (o1.getDisplayName().toString()).compareTo(o2.getDisplayName().toString());
        return 1;*/
        int compareValue = (o1.getRespectiveResultSortingIndex()).compareTo(o2.getRespectiveResultSortingIndex());
        if(compareValue==0){
            return (o1.getDisplayName()).compareTo(o2.getDisplayName());
        }
        return compareValue;

    }
}
