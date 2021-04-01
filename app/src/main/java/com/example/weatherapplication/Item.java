package com.example.weatherapplication;

public class Item {
    private String gubun,
            deathCnt,
            incDec,
            defCnt,
            isolIngCnt,
            localOccCnt,
            url_;


    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public String getDeathCnt() {
        return deathCnt;
    }

    public void setDeathCnt(String deathCnt) {
        this.deathCnt = deathCnt;
    }

    public String getIncDec() {
        return incDec;
    }

    public void setIncDec(String incDec) {
        this.incDec = incDec;
    }

    public String getDefCnt() {
        return defCnt;
    }

    public void setDefCnt(String defCnt) {
        this.defCnt = defCnt;
    }

    public String getIsolIngCnt() {
        return isolIngCnt;
    }

    public void setIsolIngCnt(String isolIngCnt) {
        this.isolIngCnt = isolIngCnt;
    }

    public String getLocalOccCnt() {
        return localOccCnt;
    }

    public void setLocalOccCnt(String localOccCnt) {
        this.localOccCnt = localOccCnt;
    }


    @Override
    public String toString() {
        return "Item{" +
                "gubun='" + gubun + '\'' +
                ", deathCnt='" + deathCnt + '\'' +
                ", incDec='" + incDec + '\'' +
                ", defCnt='" + defCnt + '\'' +
                ", isolIngCnt='" + isolIngCnt + '\'' +
                ", localOccCnt='" + localOccCnt + '\'' + '}';
    }
}
