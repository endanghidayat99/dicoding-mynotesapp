package id.co.endang.consumerapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.co.endang.consumerapp.entity.Note;
import id.co.endang.consumerapp.CustomOnItemClickListener;
import id.co.endang.consumerapp.NoteAddUpdateActivity;
import id.co.endang.consumerapp.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private ArrayList<Note> listNotes = new ArrayList<>();
    private Activity activity;

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Note> getListNotes() {
        return listNotes;
    }

    public void setListNotes(ArrayList<Note> listNotes) {
        if (listNotes.size() > 0)
            this.listNotes.clear();
        this.listNotes.addAll(listNotes);
        notifyDataSetChanged();
    }

    public void addItem(Note note) {
        this.listNotes.add(note);
        notifyItemInserted(listNotes.size() - 1);
    }

    public void updateItem(int position, Note note) {
        this.listNotes.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position) {
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNotes.size());
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,parent,false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        final Note note = listNotes.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvDate.setText(note.getDate());
        holder.tvDescription.setText(note.getDescription());
        holder.cvNote.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION,position);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE,note);
                activity.startActivityForResult(intent,NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription,tvDate;
        CardView cvNote;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            cvNote = itemView.findViewById(R.id.cv_item_note);
        }
    }
}
