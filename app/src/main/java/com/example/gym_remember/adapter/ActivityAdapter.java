package com.example.gym_remember.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_remember.R;
import com.example.gym_remember.localstorage.StorageServices;
import com.example.gym_remember.model.Activity;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<Activity> activityList;
    private StorageServices storageServices = StorageServices.getInstance();

    public ActivityAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_activities, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        holder.title.setText(activity.getTitle());
        holder.times.setText(activity.getNumberTimes() + " Vezes");

        holder.checkboxContainer.removeAllViews(); // clear previous

        for (int i = 1; i <= activity.getRepetitions(); i++) {
            final int repetitionIndex = i; // capture final copy for lambda

            CheckBox checkBox = new CheckBox(holder.itemView.getContext());
            checkBox.setText("Repetição " + repetitionIndex);

            checkBox.setOnClickListener(v -> {
                boolean checked = ((CheckBox) v).isChecked();
                Context context = v.getContext();

                if (checked && activity.getRelaxationMinutes() > 0) {
                    showFloatingCountdown((ViewGroup) holder.itemView, activity.getRelaxationMinutes(), context);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 4, 0, 4);
            checkBox.setLayoutParams(params);

            holder.checkboxContainer.addView(checkBox);
        }

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            String titleToDelete = activityList.get(currentPosition).getTitle();

            storageServices.deleteActivityAsync(v.getContext(), titleToDelete, () -> {
                activityList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                Toast.makeText(v.getContext(), "Activity deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showFloatingCountdown(ViewGroup parent, int relaxationMinutes, Context context) {
        final TextView countdownText = new TextView(context);
        countdownText.setBackgroundColor(Color.BLACK);
        countdownText.setTextColor(Color.WHITE);
        countdownText.setPadding(16, 8, 16, 8);
        countdownText.setTextSize(16);
        countdownText.setText("Starting...");

        final PopupWindow popup = new PopupWindow(countdownText,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(false);
        popup.setFocusable(false);

        parent.post(() -> popup.showAtLocation(parent, Gravity.CENTER, 0, 0));

        final long totalMillis = relaxationMinutes * 60 * 1000L;
        final long interval = 1000L;

        new CountDownTimer(totalMillis, interval) {
            long elapsed = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                elapsed += interval;
                long seconds = (elapsed / 1000) % 60;
                long minutes = (elapsed / 1000) / 60;
                countdownText.setText(String.format("Relaxation: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                popup.dismiss();
                Toast.makeText(context, "Relaxation over!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView title, times;
        LinearLayout checkboxContainer;
        ImageButton btnDelete;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            times = itemView.findViewById(R.id.times);
            checkboxContainer = itemView.findViewById(R.id.checkboxContainer);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
