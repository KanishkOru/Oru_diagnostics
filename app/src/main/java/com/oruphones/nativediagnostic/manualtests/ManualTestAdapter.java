package com.oruphones.nativediagnostic.manualtests;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.PervacioTest;



import java.util.ArrayList;

/**
 * Created by Pervacio on 29/08/2017.
 */
public class ManualTestAdapter extends BaseAdapter implements View.OnClickListener {

    PervacioTest pervacioTest;
    private ArrayList<ManualTestsPOJO> _hwTestListView;
    private ManualTestsActivity manualTestsActivity;
    private CheckBox checkAll;

    public ManualTestAdapter(ManualTestsActivity manualTestsActivity, ArrayList<ManualTestsPOJO> _hwTestListView) {
        this.manualTestsActivity = manualTestsActivity;
        this._hwTestListView = _hwTestListView;
        pervacioTest = PervacioTest.getInstance();
    }

    @Override
    public int getCount() {
        return _hwTestListView.size();
    }

    @Override
    public Object getItem(int position) {
        return _hwTestListView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
//            LayoutInflater _layoutInflator = (LayoutInflater) manualTestsActivity.getSystemService
//                    (manualTestsActivity.LAYOUT_INFLATER_SERVICE);
//            convertView = _layoutInflator.inflate(R.layout.manual_list_view, null);
            viewHolder = new ViewHolder();
            Typeface tf = Typeface.createFromAsset(OruApplication.getAppContext().getAssets(), "fonts/roboto_regular.ttf");

            viewHolder.testName = (TextView) convertView.findViewById(R.id.test_name_tv);
            viewHolder.testName.setTypeface(tf);
            viewHolder.testImage = (ImageView) convertView.findViewById(R.id.images_category);
//            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.manual_test_checkbox);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ManualTestsPOJO _hwTestPojo = _hwTestListView.get(position);
        if (_hwTestPojo != null) {
            //viewHolder.testImage.setImageResource(manualTestsActivity.getResourceID(_hwTestPojo.getTestName(), BaseActivity.TEST_ICON));
            viewHolder.testName.setText(_hwTestPojo.getDisplayName());
            viewHolder.checkBox.setChecked(BaseActivity.selectedManualTests.contains(_hwTestPojo.getTestName()));
        }

        viewHolder.checkBox.setOnClickListener(this);
        viewHolder.checkBox.setTag(position);
        viewHolder.testName.setOnClickListener(this);
        viewHolder.testName.setTag(position);

        if(BaseActivity.isAssistedApp) {
            viewHolder.checkBox.setEnabled(false);
        }

        return convertView;
    }

    private void checkItemClick(int position){
        boolean testsPOJO =  _hwTestListView.get(position).getChecked();
        _hwTestListView.get(position).setChecked(!testsPOJO);
        updateSelectedTestList(_hwTestListView.get(position));
        if (BaseActivity.selectedManualTests.size() == _hwTestListView.size())
            manualTestsActivity.checkAllTests.setChecked(true);
        else
            manualTestsActivity.checkAllTests.setChecked(false);
        notifyDataSetChanged();
    }

    private void updateSelectedTestList(ManualTestsPOJO manualTestsPOJO) {
        if (manualTestsPOJO.getChecked()) {
            if (!BaseActivity.selectedManualTests.contains(manualTestsPOJO.getTestName())) {
                BaseActivity.selectedManualTests.add(manualTestsPOJO.getTestName());
            }
        } else {
            if (BaseActivity.selectedManualTests.contains(manualTestsPOJO.getTestName())) {
                BaseActivity.selectedManualTests.remove(manualTestsPOJO.getTestName());
            }
        }
    }

    @Override
    public void onClick(View v) {
        //       case R.id.manual_test_checkbox:
        if (v.getId() == R.id.test_name_tv) {
            int position = (int) v.getTag();
            checkItemClick(position);
        }
    }

    static class ViewHolder {
        TextView testName;
        CheckBox checkBox;
        ImageView testImage;

    }
}
