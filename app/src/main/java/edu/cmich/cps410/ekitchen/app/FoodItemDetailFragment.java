package edu.cmich.cps410.ekitchen.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A fragment representing a single FoodItem detail screen.
 * This fragment is either contained in a {@link FoodItemListActivity}
 * in two-pane mode (on tablets) or a {@link FoodItemDetailActivity}
 * on handsets.
 */
public class FoodItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private ContentManager.FoodItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ContentManager.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        // Enable the options menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fooditem_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            LinearLayout linearLayout = ((LinearLayout) rootView.findViewById(R.id.fooditem_detail));
            // Set the name
            ((TextView)linearLayout.findViewById(R.id.fooditem_detail_name)).setText(mItem.getName());
            // Set the expiration date
            ((TextView) linearLayout.findViewById(R.id.fooditem_detail_expiration))
                    .setText("Expiration Date: " + mItem.getExpiration());
            // Set the details
            ((TextView) linearLayout.findViewById(R.id.fooditem_detail_details))
                    .setText("Additional Details: " + mItem.getDetails());
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_activity_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_remove:
                removeFoodItem(mItem);
                return true;
            case R.id.action_edit:
                openEdit(mItem);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Opens the addItem {@link android.app.AlertDialog},
     * but with values pre-filled with existing values.
     */
    private void openEdit(ContentManager.FoodItem foodItem) {
        ContentManager contentManager = new ContentManager(getActivity());
        contentManager.addItem(foodItem);
        // Close self
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    /**
     * Deletes all instances of the provided
     * {@link edu.cmich.cps410.ekitchen.app.ContentManager.FoodItem FoodItem}.
     *
     * @param foodItem The {@link edu.cmich.cps410.ekitchen.app.ContentManager.FoodItem FoodItem}
     *                 to be deleted.
     */
    private void removeFoodItem(ContentManager.FoodItem foodItem) {
        ContentManager contentManager = new ContentManager(getActivity());
        contentManager.deleteItem(foodItem);
        // Close self
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
