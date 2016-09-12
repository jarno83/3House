package treehou.se.habit.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHThing;
import treehou.se.habit.R;

public class ThingAdapter extends RecyclerView.Adapter<ThingAdapter.ThingHolder> {

    private static String TAG = ThingAdapter.class.getSimpleName();

    private List<OHThing> items = new ArrayList<>();

    private ItemListener itemListener = new DummyItemListener();

    public class ThingHolder extends RecyclerView.ViewHolder {
        private final TextView lblThing;

        public ThingHolder(View view) {
            super(view);
            lblThing = (TextView) view.findViewById(R.id.lbl_thing);
        }

        public void update(OHThing thing){
            lblThing.setText(thing.getLabel());
            itemView.setOnClickListener(view -> itemListener.onItemClickListener(thing));
        }
    }

    public ThingAdapter() {
    }

    @Override
    public ThingHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ThingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_thing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ThingHolder holder, final int position) {
        final OHThing item = items.get(position);
        holder.update(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemListener(ItemListener itemListener) {
        if (itemListener == null) {
            this.itemListener = new DummyItemListener();
            return;
        }
        this.itemListener = itemListener;
    }

    public void addItem(OHThing item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void addAll(List<OHThing> items) {
        for (OHThing item : items) {
            this.items.add(0, item);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Log.d(TAG, "removeItem: " + position);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(OHThing item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Remove all items from adapter
     */
    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public interface ItemListener {

        void onItemClickListener(OHThing item);

        boolean onItemLongClickListener(OHThing item);
    }

    private class DummyItemListener implements ItemListener {

        @Override
        public void onItemClickListener(OHThing item) {
        }

        @Override
        public boolean onItemLongClickListener(OHThing item) {
            return false;
        }
    }
}
