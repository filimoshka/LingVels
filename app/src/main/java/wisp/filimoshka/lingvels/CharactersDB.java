package wisp.filimoshka.lingvels;

public class CharactersDB implements Comparable {

    public String character_name;
    public String character_img;
    public int id_character;
    public boolean main_character, minor_character;

    public CharactersDB() {
    }

    public CharactersDB(String character_name, String character_img, int id_character,
                        boolean main_character, boolean minor_character) {
        this.character_name = character_name;
        this.character_img = character_img;
        this.id_character = id_character;
        this.main_character = main_character;
        this.minor_character = minor_character;
    }

    public int getId() {
        return id_character;
    }

    @Override
    public int compareTo(Object o) {
        int compareId=((CharactersDB)o).getId();
        return this.id_character-compareId;

    }
}
