package com.smehsn.compassinsurance.parser;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class StrategyProvider {
    protected static  Map<String, StringConverter> strategyMap = new HashMap<>();
    static {
        strategyMap.put("text_view", new TextViewConverter());
        strategyMap.put("edit_text", new EditTextConverter());
        strategyMap.put("button", new ButtonConverter());
        strategyMap.put("spinner", new SpinnerConverter());
    }

    public static StringConverter get(String name){
        if (!strategyMap.containsKey(name))
             throw new IllegalArgumentException("No stringConverter with name " + name);
        return strategyMap.get(name);
    }


    private static final class TextViewConverter implements StringConverter{

        @Override
        public String convertViewToString(View view) {
            if (view instanceof TextView){
                TextView textView = (TextView) view;
                return textView.getText().toString();
            }
            else throw new IllegalArgumentException( view.getTag() + " cannot apply "+ this.getClass().getSimpleName()+ " StringConverter for this view");
        }
    }


    private static final class EditTextConverter implements StringConverter{

        @Override
        public String convertViewToString(View view) {
            if (view instanceof EditText){
                EditText editText = (EditText) view;
                return editText.getText().toString();
            }
            else throw new IllegalArgumentException( view.getTag() + ": cannot apply "+ this.getClass().getSimpleName()+ " StringConverter for this view");
        }
    }

    private static final class ButtonConverter implements StringConverter{

        @Override
        public String convertViewToString(View view) {
            if (view instanceof Button){
                Button button = (Button) view;
                return button.getText().toString();
            }
            else throw new IllegalArgumentException( view.getTag() + ": cannot apply "+ this.getClass().getSimpleName()+ " stringConverter for this view");
        }
    }

    private static final class SpinnerConverter implements StringConverter{

        @Override
        public String convertViewToString(View view) {
            if (view instanceof Spinner){
                Spinner spinner =  (Spinner) view;
                int selection = spinner.getSelectedItemPosition();
                if (selection == 0)
                    return "";
                else return spinner.getSelectedItem().toString();

            }
            else throw new IllegalArgumentException( view.getTag() + ": cannot apply "+ this.getClass().getSimpleName()+ " stringConverter for this view");
        }
    }



//    private static final class Converter implements StringConverter{
//
//        @Override
//        public String convertViewToString(View view) {
//            if (view instanceof ){
//
//            }
//            else throw new IllegalArgumentException( view.getTag() + ": cannot apply "+ this.getClass().getSimpleName()+ " stringConverter for this view");
//        }
//    }

}
