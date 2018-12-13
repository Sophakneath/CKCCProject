package com.example.phakneath.ckcc_homework.Data;

import java.util.ArrayList;
import java.util.List;

import com.example.phakneath.ckcc_homework.Entity.person;

public class PersonInMemory {

    List<person> p;
    public PersonInMemory()
    {
        p = new ArrayList<>();
        p.add(new person("Admin", "Admin", "Admin"));
    }

    public void savePerson(person per){ p.add(per);}

    public person getPerson(String username)
    {
        for(person per:p)
        {
            if(per.getUsername().equals(username))
            {
                return per;
            }
        }
        return  null;
    }

    public List<person> getPersons()
    {
        return  p;
    }
}
