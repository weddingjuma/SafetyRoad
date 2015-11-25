package org.android.safetyroad;

import java.text.Collator;
import java.util.Comparator;


/**
 * Created by Administrator on 2015-11-10.
 */
public class ListData {
    // Phone Name
    public String mName;

    // Phone Number
    public String mNumber;

    // Alphabet Sort
    public  static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData lhs, ListData rhs) {
            return sCollator.compare(lhs.mName, rhs.mName);
        }
    };


}
