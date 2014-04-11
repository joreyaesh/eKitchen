package edu.cmich.cps410.ekitchen.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContentManager {
    
    public static String filename = "content.txt";
    
    private Context mContext = null;

    public ContentManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Prompts the user to add a item using an {@link android.app.AlertDialog}, 
     * and then calls {@link #saveItemToFile(String, String)}
     * to save the new item to content.txt.
     * @see #saveItemToFile(String, String)
     * @see #deleteItem(edu.cmich.cps410.ekitchen.app.ContentManager.FoodItem)
     */
    public void addItem() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        final LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(mContext);
//        nameBox.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        nameBox.setHint("Item Name");
        layout.addView(nameBox);

        final EditText expirationBox = new EditText(mContext);
//        expirationBox.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        expirationBox.setHint("Expiration Date");
        layout.addView(expirationBox);

        alert.setTitle("Add New Item");

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = nameBox.getText().toString();
                if(name == null || name.equals("")) name = "Undefined Name";
                String expiration = expirationBox.getText().toString();
                if(expiration == null || expiration.equals("")) expiration = "Undefined";

                // Save the provided string as a new line in content.txt.
                saveItemToFile(name, expiration);
                // Add the item to our list of items.
                FoodItem foodItem = new FoodItem(name, expiration);
                ITEMS.add(foodItem);
                ITEM_MAP.put(FoodItem.getUnusedId(),foodItem);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled. Do nothing.
            }
        });

        alert.show();
    }

    /**
     * Adds a line to the end of content.txt containing the name and expiration date given.
     * @param name The name of the item to add.
     * @param expiration The item's expiration date.
     * @return True if the item was added, False otherwise
     * @see #addItem()
     */
    private boolean saveItemToFile(String name, String expiration){
        boolean success = true;
        try {
            FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_APPEND);
            String item = name + "\t" + expiration;
            fos.write(item.getBytes());
            fos.write(System.getProperty("line.separator").getBytes());
            fos.close();
        }
        catch (Exception e) {
            success = false;
            toast("Error saving item");
        }
        if(success) {
            toast("Item successfully saved");
        }
        return success;
    }

    /**
     * Finds the line in items.txt which corresponds to the {@link String name}
     * given, and calls {@link #removeItemFromFile(int)} to delete the line from content.txt.
     * @param foodItem The {@link edu.cmich.cps410.ekitchen.app.ContentManager.FoodItem FoodItem} to delete
     * @return True if the item was deleted, False otherwise
     * @see #removeItemFromFile(int)
     * @see #addItem()
     */
    public boolean deleteItem(FoodItem foodItem){
        boolean found = false;
        int lineNumber = 1;
        try {
            FileInputStream fis = mContext.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            line = reader.readLine();
            while (line != null){
                if((line.split("\t")[0].contains(foodItem.getName())
                        && (line.split("\t")[1].contains(foodItem.getExpiration())))){
                    // Item was found.
                    found = true;
                    // Delete the line containing the item.
                    removeItemFromFile(lineNumber);
                    // Remove the FoodItem from memory.
                    ITEM_MAP.remove(foodItem.id);
                    ITEMS.remove(foodItem);
                    break;
                }
                line = reader.readLine();
                lineNumber += 1;
            }
            reader.close();
            fis.close();
        } catch (Exception e) {
            toast("Error deleting item");
        }
        if(found){
            toast("Item successfully deleted");
        }
        return found;
    }

    /**
     * Removes the given line from content.txt.
     * @param lineNumber The line to delete from the file
     * @throws java.io.IOException If an error occurred when editing the file.
     * @see #deleteItem(edu.cmich.cps410.ekitchen.app.ContentManager.FoodItem)
     */
    private void removeItemFromFile(int lineNumber) {
        FileOutputStream tmp = null;
        FileInputStream fis = null;
        try {
            tmp = mContext.openFileOutput("tmp", Context.MODE_APPEND);
            fis = mContext.openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tmp));

            for (int i = 1; i < lineNumber; i++)
                // Copy lines from old file to new file, until the line to delete is reached.
                bw.write(String.format("%s%n", br.readLine()));

            br.readLine();

            String line;
            while (null != (line = br.readLine()))
                bw.write(String.format("%s%n", line));

            br.close();
            bw.close();
;
            // Rename the new file to the old file's filename, overwriting the old file.
            File oldFile = mContext.getFileStreamPath(filename);
            File newFile = mContext.getFileStreamPath("tmp");
            newFile.renameTo(oldFile);
        } catch (IOException e) {}

    }

    /**
     * Displays the given text as a toast.
     * @param text The text to display as a toast
     */
    public void toast(String text){
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.show();
    }




    /**
     * An array of FoodItems.
     */
    public static List<FoodItem> ITEMS = new ArrayList<FoodItem>();

    /**
     * A map of FoodItems, by ID.
     */
    public static Map<String, FoodItem> ITEM_MAP = new HashMap<String, FoodItem>();

    /**
     * This Object which represents any item of food entered into the app.
     */
    public static class FoodItem {
        public String id;
        public String name;
        public String expiration;

        public FoodItem(String name) {
            this(name, "Unspecified");
        }

        public FoodItem(String name, String expiration) {
            this.id = getUnusedId();
            this.name = name;
            this.expiration = expiration;
        }

        /**
         * Checks for the first available id, and returns it.
         * @return The first available id.
         */
        public static String getUnusedId() {
            int n = 1;
            while(true){
                if(!ITEM_MAP.containsKey(String.valueOf(n))) {
                    return String.valueOf(n);
                }
                else {
                    n++;
                }
            }
        }

        @Override
        public String toString() {
            return name;
        }
        public String getName() {
            return name;
        }
        public String getExpiration() {
            return expiration;
        }
    }
}
