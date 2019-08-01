package com.example.firon.suwarippa_rhythm_java;

public class Ranking implements Comparable<Ranking>{//[20]
    private int rank;//[21]
    private String name;//[22]
    public Ranking(int rank, String name) {//[23]
        this.rank = rank;//[24]
        this.name = name;//[25]
    }
    public String name() {//[26]
        return this.name;//[27]
    }
    public int rank() {//[28]
        return this.rank;//[29]
    }

    public int compareTo( Ranking com ){//[11]
        return this.rank() - com.rank();//[12]
    }

    public String toString(){
        return "Name : " + name + "\nRank : " + rank;
    }
}