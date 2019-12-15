package com.miage.gruppetto.ui.locations.dummy;

import android.util.Log;

import com.miage.gruppetto.data.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miage.gruppetto.ui.home.HomeFragment.locations;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        Log.d("locations:DummyContent","static");
        // Add some sample items.
        ArrayList<String> myLocation = new ArrayList<>();
        boolean locationAllreadyAdded = false;
        int i=0;
        for (Location location : locations ) {
            String locationLabel="("+location.getLat()+","+location.getLng()+")";
            while ( i < myLocation.size() && !locationAllreadyAdded) {
                if (locationLabel.equals(myLocation.get(i))) {
                    locationAllreadyAdded=true;
                }
                i++;
            }
            if (!locationAllreadyAdded) {
                myLocation.add(locationLabel);
                addItem(createDummyItem(location));
            }
            locationAllreadyAdded=false;
            i=0;
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(Location location) {
        return new DummyItem("1","("+location.getLat()+","+location.getLng()+")","détails");
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
