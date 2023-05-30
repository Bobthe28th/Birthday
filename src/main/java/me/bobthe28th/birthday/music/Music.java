package me.bobthe28th.birthday.music;

public class Music {

    String name;
    Long length;

    public Music(String name, Long length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public Long getLength() {
        return length;
    }
}
