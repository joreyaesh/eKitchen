package edu.cmich.cps410.ekitchen.app.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add sample items.
        addItem(new DummyItem("1", "Bread", "Expires on 4/14/14"));
        addItem(new DummyItem("2", "Peanut Butter"));
        addItem(new DummyItem("3", "Jelly"));
        addItem(new DummyItem("4", "Eggs", "Expires on 4/14/18"));
        addItem(new DummyItem("5", "Flour"));
        addItem(new DummyItem("6", "Milk", "EXPIRED on 3/28/14"));
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String name;
        public String info;

        public DummyItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public DummyItem(String id, String name, String info) {
            this.id = id;
            this.name = name;
            this.info = info;
        }

        @Override
        public String toString() { return name; }
        public String getName() {
            return name;
        }
        public String getInfo() { return info; }
    }
}
