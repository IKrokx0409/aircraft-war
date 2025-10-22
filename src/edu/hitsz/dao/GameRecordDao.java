package edu.hitsz.dao;

import java.util.List;

public interface GameRecordDao {
    void addRecord(GameRecord record, String difficulty);
    List<GameRecord> getAllRecords(String difficulty);
    void deleteRecord(String playerName, int score, String time, String difficuly);
}