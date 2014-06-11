package Bean;

import java.util.Date;

public class Tweet {
	String screenName = "";
    String title = "";
    Long status_id = 0L;
    Date published_date = null;
  
    public String getScreenName() {
        return screenName;
    }
 
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
    public Long getStatusId() {
        return status_id;
    }
 
    public void setStatusId(Long status_id) {
        this.status_id = status_id;
    }
 
    public Date getPublishedDate() {
        return published_date;
    }
 
    public void setPublishedDate(Date published_date) {
        this.published_date = published_date;
    }
}
