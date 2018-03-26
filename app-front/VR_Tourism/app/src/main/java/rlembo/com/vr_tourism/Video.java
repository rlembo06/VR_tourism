package rlembo.com.vr_tourism;

/**
 * Created by lr706510 on 26/03/2018.
 */

public class Video {

    private String lien_video;

    public Video() {}

    public Video (String lien_video){
        this.lien_video = lien_video;
    }

    public String getLien_video(){
        return this.lien_video;
    }

    public void setLien_video(String lien_video){
        this.lien_video = lien_video;
    }
}
