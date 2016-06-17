package com.smehsn.compassinsurance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.smehsn.compassinsurance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sam
 */
public class SegmentedEditText extends LinearLayout {

    private List< Segment> segments;

    public SegmentedEditText(Context context) {
        super(context);
    }

    public SegmentedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        ArrayList<String> editTextValues =  new ArrayList<>();
        for (Segment segment: segments)
            editTextValues.add(segment.text.getText().toString());
        bundle.putStringArrayList("editTextValues", editTextValues);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            ArrayList<String> values = bundle.getStringArrayList("editTextValues");
            if (values != null) {
                for (int i = 0; i < values.size(); ++i)
                    segments.get(i).text.setText(values.get(i));
            }
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    private void parseAttributes(AttributeSet attributeSet){
        TypedArray attrArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.SegmentedEditText);
        Map<Integer, Object> attrs = new TreeMap<>();

        for (int i = 0; i < attrArray.length(); i++) {
            int attr = attrArray.getIndex(i);
            switch(attr){
                case R.styleable.SegmentedEditText_segments:
                    attrs.put(attr, attrArray.getString(attr));
                    break;
                case R.styleable.SegmentedEditText_editTextView:
                    attrs.put(attr, attrArray.getResourceId(attr, R.layout.boxed_edit_text));
                    break;
            }
        }
        initSegments(
                (String) attrs.get(R.styleable.SegmentedEditText_segments),
                (Integer)attrs.get(R.styleable.SegmentedEditText_editTextView)
        );

        attrArray.recycle();
    }

    private void initSegments(String string, int layoutResId){
        String[] words = string.split("\\s+");
        segments = new ArrayList<>();

        for (int pos=0; pos<words.length; ++pos){
            String word = words[pos];
            Segment segment = new Segment();

            int maxChars = Integer.parseInt(word);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            EditText editText  = (EditText) inflater.inflate(layoutResId, this, false);
            editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxChars) });
            editText.setHint(String.format("%0" + maxChars + "d", 0).replace("0","#"));
            editText.setHintTextColor(Color.TRANSPARENT);

            segment.maxChars = maxChars ;
            segment.text = editText;
            segment.position = pos;
            segments.add(segment);
            this.addView(editText);

        }

        for (int i=0; i<segments.size();++i){
            Segment segment = segments.get(i);
            EditText curr = segment.text;
            EditText next = (i >= segments.size()-1) ? null : segments.get(i+1).text;
            EditText prev = (i <= 0) ?                 null : segments.get(i-1).text;
            TextChangeListener listener = new TextChangeListener(prev, next, segment.maxChars);
            curr.addTextChangedListener(listener);
            curr.setOnKeyListener(listener);
        }

    }



    public String getText(){return getText("");}

    public String getText(String delimeter){
        StringBuilder str = new StringBuilder();
        for (int i=0; i<segments.size(); ++i){
            str.append(segments.get(i).text.getText().toString()).append( (i!=segments.size()-1)? delimeter : "" );
        }
        return str.toString();
    }


    private static class Segment{
        int      maxChars;
        EditText text;
        int      position;
    }





    private static class TextChangeListener implements TextWatcher, View.OnKeyListener{
        private int maxChars;
        private EditText prev;
        private EditText next;

        private TextChangeListener(EditText prev, EditText next,int maxChars) {
            this.prev = prev;
            this.next = next;
            this.maxChars = maxChars;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() >= maxChars && next != null) {
               focusNext();
            }
            else if (s.length() == 0 && prev != null) {
               focusPrev();
            }
        }

        private void focusNext(){
            next.requestFocus();
            next.setSelection(0);
        }
        private void focusPrev(){
            prev.requestFocus();
            prev.setSelection(prev.getText().length());
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL){
                EditText text = (EditText)v;
                if (text.getSelectionStart()== 0 && prev != null){
                    focusPrev();
                }
            }
            return false;
        }
    }
}
