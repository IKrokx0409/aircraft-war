package edu.hitsz.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameRecordDaoImpl implements GameRecordDao {

//    private static final String FILENAME = "records.dat";
    private String getFilenameForDifficulty(String difficulty) {
        return "records_" + difficulty.toLowerCase() + ".dat";
    }

    @Override
    public void addRecord(GameRecord record, String difficulty) {
        List<GameRecord> records = getAllRecords(difficulty);
        records.add(record);
        saveRecords(records, difficulty);
    }

    @Override
    public List<GameRecord> getAllRecords(String difficulty) {
        String filename = getFilenameForDifficulty(difficulty);
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<GameRecord>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveRecords(List<GameRecord> records, String difficulty) {
        String filename = getFilenameForDifficulty(difficulty);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRecord(String playerName, int score, String time, String difficulty) {
        List<GameRecord> records = getAllRecords(difficulty);
        boolean removed = records.removeIf(record ->
                record.getPlayerName().equals(playerName) &&
                        record.getScore() == score &&
                        record.getFormattedTimestamp().equals(time)
        );
        if(removed) {
            saveRecords(records, difficulty);
            System.out.println("Record deleted: " + playerName + ", " + score + ", " + time);
        } else {
            System.out.println("Record to be deleted not found");
        }
    }

}