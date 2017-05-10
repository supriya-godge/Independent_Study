package engine;

/**
 * Created by sup33 on 4/18/2017.
 */
public class PlayerInfo{
    private int id;
    private String mark;
    private boolean isLocal;


    public PlayerInfo(String mark){
        this.mark = mark;
    }

    public String toString(){
        return "Id:"+id+" Mark:"+mark;
    }

    public boolean getisLocal(){
        return isLocal;
    }

    public int getId(){
        return id;
    }

    public String getMark(){
        return mark;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setIsLocal(boolean local) {
        isLocal = local;
    }

    public boolean getIsLocal(){
        return isLocal;

    }
}
