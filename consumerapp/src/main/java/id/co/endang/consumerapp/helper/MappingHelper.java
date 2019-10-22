package id.co.endang.consumerapp.helper;

import android.database.Cursor;

import java.util.ArrayList;

import id.co.endang.consumerapp.db.DatabaseContract;
import id.co.endang.consumerapp.entity.Note;

public class MappingHelper {

    public static ArrayList<Note> mapCursorToArrayList(Cursor notesCursor){
        ArrayList<Note> noteList = new ArrayList<>();

        while (notesCursor.moveToNext()){
            int id = notesCursor.getInt(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums._ID));
            String title = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.TITLE));
            String description = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.DESCRIPTION));
            String date = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.DATE));
            noteList.add(new Note(id,title,description,date));
        }
        return noteList;
    }

    public static Note mapCursorToObject(Cursor notesCursor){
        notesCursor.moveToFirst();
        int id = notesCursor.getInt(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums._ID));
        String title = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.TITLE));
        String descr = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.DESCRIPTION));
        String date = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColums.DATE));

        return new Note(id,title,descr,date);
    }
}
