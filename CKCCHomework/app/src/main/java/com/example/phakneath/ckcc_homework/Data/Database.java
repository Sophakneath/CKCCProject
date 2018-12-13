package com.example.phakneath.ckcc_homework.Data;

public class Database {

    private static PersonInMemory repository;
    public static PersonInMemory getPersonInMemory()
    {
        if(repository == null)
        {
            repository = new PersonInMemory();
        }

        return repository;
    }
}
