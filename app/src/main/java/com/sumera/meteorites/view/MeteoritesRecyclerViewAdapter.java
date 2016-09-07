package com.sumera.meteorites.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumera.meteorites.R;
import com.sumera.meteorites.model.Meteorite;

import java.util.List;

/**
 * Created by martin on 05/09/16.
 */

public class MeteoritesRecyclerViewAdapter extends RecyclerView.Adapter<MeteoritesRecyclerViewAdapter.MeteoriteViewHolder> {

    public interface OnMeteoriteClickedListener {
        void onMeteoriteClicked(Meteorite meteorit, int position);
    }

    private OnMeteoriteClickedListener m_onRecyclerViewItem = null;

    private List<Meteorite> m_meteorites;

    public MeteoritesRecyclerViewAdapter(List<Meteorite> meteorites) {
        m_meteorites = meteorites;
    }

    public void setNewData(List<Meteorite> meteorites) {
        m_meteorites = meteorites;
        notifyDataSetChanged();
    }

    public void setOnRecyclerViewItemClickListener(OnMeteoriteClickedListener listener) {
        m_onRecyclerViewItem = listener;
    }

    @Override
    public MeteoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meteorite_list_item, parent, false);
        return new MeteoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeteoritesRecyclerViewAdapter.MeteoriteViewHolder holder, final int position) {
        holder.setItem(m_meteorites.get(position));
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_onRecyclerViewItem != null) {
                    m_onRecyclerViewItem.onMeteoriteClicked(m_meteorites.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_meteorites.size();
    }

    public class MeteoriteViewHolder extends RecyclerView.ViewHolder {

        public Meteorite m_meteorite;

        private final View m_rootView;

        private final TextView m_nameTv;

        private final TextView m_massTv;

        public MeteoriteViewHolder(View view) {
            super(view);
            m_rootView = view;
            m_nameTv = (TextView) view.findViewById(R.id.name);
            m_massTv = (TextView) view.findViewById(R.id.mass);
        }

        public void setItem(Meteorite meteorite) {
            m_meteorite = meteorite;
            m_nameTv.setText(meteorite.getName());
            m_massTv.setText("" + meteorite.getMass());
        }

        public View getRootView() {
            return m_rootView;
        }

    }
}

