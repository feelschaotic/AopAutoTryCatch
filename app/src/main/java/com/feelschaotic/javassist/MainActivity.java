package com.feelschaotic.javassist;

import android.app.Activity;
import android.os.Bundle;

import com.feelschaotic.aopapplication.R;
import com.feelschaotic.javassist.annotations.AutoTryCatch;

import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catchAllException();
        catchNullPointerException();
        catchNullPointerAndClassCastException();
    }

    /**
     * 无崩溃，会catch所有Exception
     */
    @AutoTryCatch
    public void catchAllException() {
        int i = 1 / 0;
    }

    /**
     * 此处崩溃，因为只会catch指定的NullPointerException
     */
    @AutoTryCatch(NullPointerException.class)
    public void catchNullPointerException() {
       // int i = 1 / 0;
    }

    /**
     * 无崩溃
     */
    @AutoTryCatch({NullPointerException.class, ArithmeticException.class})
    public void catchNullPointerAndClassCastException() {
        List arrays = null;
        int size = arrays.size();
        size = 1 / 0;
    }

}
