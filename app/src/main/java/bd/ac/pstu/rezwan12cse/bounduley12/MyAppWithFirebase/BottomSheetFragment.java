package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.ac.pstu.rezwan12cse.bounduley12.R;

/**
 * Created by Rezwan on 19-07-18.
 */

public class BottomSheetFragment  extends BottomSheetDialogFragment{
    public BottomSheetFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
