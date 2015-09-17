package it.jaschke.alexandria;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by davi on 8/28/15.
 */
public class BarcodeFragment extends Fragment implements ZBarScannerView.ResultHandler  {
    private AddBook addBook;
    private ZBarScannerView mScannerView;

    public static BarcodeFragment newInstance(AddBook addBook){
        BarcodeFragment barcodeFragment = new BarcodeFragment();
        barcodeFragment.addBook = addBook;
        return barcodeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mScannerView = new ZBarScannerView(getActivity());
        List<BarcodeFormat> barcodeFormatList = new ArrayList<>();
//        barcodeFormatList.add(BarcodeFormat.ISBN10);
        //barcodeFormatList.add(BarcodeFormat.ISBN13);
        barcodeFormatList.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(barcodeFormatList);
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(getActivity(), "Contents = " + rawResult.getContents() +
//                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
//        mScannerView.startCamera();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ean",rawResult.getContents());
        editor.apply();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
