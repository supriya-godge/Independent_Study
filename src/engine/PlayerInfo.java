package engine;

/**
 * Created by sup33 on 4/18/2017.
 */
public class PlayerInfo{
    int id;
    String mark;
    boolean isLocal;


    public PlayerInfo(String mark){
        this.mark = mark;
    }

    public String toString(){
        return "Id:"+id+" Mark:"+mark;
    }

    public boolean getisLocal(){
        return isLocal;
    }
}
