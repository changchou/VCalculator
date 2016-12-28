package com.zhang.vcalculator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.javia.arity.SyntaxException;

import java.util.Vector;

/**
 * Created by Mr.Z on 2016/12/28 0028.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private Vector<HistoryEntry> entries;
    private LayoutInflater inflater;
    private Logic eval;
    //保存History的实例到全局变量中
    private final History history;

    HistoryRecyclerAdapter(Context context, History history, Logic evaluator) {
        this.entries = history.mEntries;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eval = evaluator;
        this.history = history;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.history_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryEntry entry = entries.elementAt(position);
        String base = entry.getBase();
        holder.expr.setText(base);

        try {
            String res = eval.evaluate(base);
            holder.result.setText(res);
        } catch (SyntaxException e) {
            holder.result.setText("");
        }

        if (getItemCount() == 1) {
            holder.bg.setBackgroundResource(R.drawable.history_item_bg_single);
        } else if (position == getItemCount() - 1) {
            holder.bg.setBackgroundResource(R.drawable.history_item_bg_bottom);
        } else if (position == 0) {
            holder.bg.setBackgroundResource(R.drawable.history_item_bg_top);
        } else {
            holder.bg.setBackgroundResource(R.drawable.history_item_bg_middle);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size() - 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View bg;
        TextView expr;
        TextView result;

        public ViewHolder(View itemView) {
            super(itemView);

            bg = itemView.findViewById(R.id.bg_holder);
            expr = (TextView) itemView.findViewById(R.id.historyExpr);
            result = (TextView) itemView.findViewById(R.id.historyResult);
        }
    }

    public void remove(int pos) {
        history.remove(pos);
    }

    public void removeAll() {
        history.clear();
    }

    public void addAll() {
        StringBuilder sb = new StringBuilder();
        for (HistoryEntry entry : entries) {
            if (entry.getEdited().equals("")) {
                break;
            }

            sb.append(entry.getEdited() + "+");
        }

        String strToEvalute = sb.toString();
        while (strToEvalute.endsWith("+")) {
            strToEvalute = strToEvalute.substring(0, sb.length() - 1);
        }
        eval.evaluateAndShowResult(strToEvalute,CalculatorDisplay.Scroll.UP);
    }
}
