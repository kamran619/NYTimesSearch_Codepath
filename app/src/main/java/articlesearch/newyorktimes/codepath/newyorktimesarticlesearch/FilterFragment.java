package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnFragmentInteractionListener mListener;
    private IFilterFragmentCallback mCallback;

    @Bind(R.id.etBegin)
    EditText etBegin;
    @Bind(R.id.spSort)
    Spinner spSort;
    @Bind(R.id.cbArts)
    CheckBox cbArts;
    @Bind(R.id.cbFashionAndStyle)
    CheckBox cbFashionAndStyle;
    @Bind(R.id.cbSports)
    CheckBox cbSports;

    private NYTimesSearchFilter mSearchFilter;

    public interface IFilterFragmentCallback {
        public void onFiltersSaved(NYTimesSearchFilter filter);
    }

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilterFragment.
     */
    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    private void loadFilters() {
        mSearchFilter = Parcels.unwrap(getArguments().getParcelable("filter"));
        etBegin.setText(mSearchFilter.getBeginDateAsString());
        ArrayAdapter adapter = (ArrayAdapter) spSort.getAdapter();
        String sortFilterString = capitalize(mSearchFilter.getSortOrder().getName());
        int spinnerPosition = adapter.getPosition(sortFilterString);
        spSort.setSelection(spinnerPosition);
        boolean isChecked = mSearchFilter.isNewsDeskEnabled(NYTimesSearchFilter.NewsDesks.ARTS);
        cbArts.setChecked(isChecked);
        isChecked = mSearchFilter.isNewsDeskEnabled(NYTimesSearchFilter.NewsDesks.FASHION_AND_STYLE);
        cbFashionAndStyle.setChecked(isChecked);
        isChecked = mSearchFilter.isNewsDeskEnabled(NYTimesSearchFilter.NewsDesks.SPORTS);
        cbSports.setChecked(isChecked);
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setupSpinner();
        loadFilters();
    }

    private void setupSpinner() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Newest");
        list.add("Oldest");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);
        spSort.setAdapter(adapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (context instanceof IFilterFragmentCallback) {
            mCallback = (IFilterFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFilterFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mCallback = null;
    }


    @OnClick(R.id.etBegin)
    public void onBeginTimeClicked() {
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) this;
        // The fragments activity will notify us with the new date value
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), listener, 2016, 02, 12);
        datePickerDialog.show();
    }

    @OnClick(R.id.btnSave)
    public void onSaveClicked() {
        saveFilters();
        dismiss();
    }

    @OnCheckedChanged({R.id.cbArts, R.id.cbFashionAndStyle, R.id.cbSports})
    public void onCheckboxChanged(CompoundButton view, boolean checked) {
        NYTimesSearchFilter.NewsDesks newsDeskToUpdate = null;
        switch (view.getId()) {
            case R.id.cbSports:
                newsDeskToUpdate = NYTimesSearchFilter.NewsDesks.SPORTS;
                break;
            case R.id.cbFashionAndStyle:
                newsDeskToUpdate = NYTimesSearchFilter.NewsDesks.FASHION_AND_STYLE;
                break;
            case R.id.cbArts:
                newsDeskToUpdate = NYTimesSearchFilter.NewsDesks.ARTS;
                break;
        }
        mSearchFilter.updateNewsDesk(newsDeskToUpdate, checked);
    }

    private void saveFilters() {
        NYTimesSearchFilter filter = mSearchFilter;
        filter.setBeginDate(etBegin.getText().toString());
        String sortOrderString = spSort.getSelectedItem().toString();
        NYTimesSortOrder sortOrder = NYTimesSortOrder.NEWEST;
        if (sortOrderString.equalsIgnoreCase("newest")) {
            sortOrder = NYTimesSortOrder.NEWEST;
        } else if (sortOrderString.equalsIgnoreCase("oldest")) {
            sortOrder = NYTimesSortOrder.OLDEST;
        }
        filter.setSortOrder(sortOrder);

        boolean addNewsDesk = cbArts.isChecked();
        filter.updateNewsDesk(NYTimesSearchFilter.NewsDesks.ARTS, addNewsDesk);
        addNewsDesk = cbFashionAndStyle.isChecked();
        filter.updateNewsDesk(NYTimesSearchFilter.NewsDesks.FASHION_AND_STYLE, addNewsDesk);
        addNewsDesk = cbSports.isChecked();
        filter.updateNewsDesk(NYTimesSearchFilter.NewsDesks.SPORTS, addNewsDesk);
        mCallback.onFiltersSaved(filter);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        etBegin.setText("" + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
